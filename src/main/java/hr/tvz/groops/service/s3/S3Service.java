package hr.tvz.groops.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import hr.tvz.groops.dto.response.DocumentDto;
import hr.tvz.groops.dto.response.DocumentSummaryDto;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.util.DateUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Dithering;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.stream.Collectors;

import static hr.tvz.groops.exception.CrudExceptionEnum.DOCUMENT_NOT_FOUND_ON_S3_BY_KEY_FOR_BUCKET;

@Service
public class S3Service {

    private final String USER = "user";
    private final String USER_PROFILE = "user-profile";
    private final String GROUP = "group";
    private final String GROUP_PROFILE = "group-profile";
    private final String POST = "post";
    private final String IMAGE_TYPE = "image";
    private final AmazonS3 s3client;
    private final String bucket;

    @Autowired
    public S3Service(
            AmazonS3 s3client,
            @Value("${aws.s3.bucket.name}") String bucket
    ) {
        this.s3client = s3client;
        this.bucket = bucket;
    }


    public @NotNull S3Object downloadDocumentByKey(String key) {
        if (!s3client.doesObjectExist(bucket, key)) {
            throw new EntityNotFoundException(DOCUMENT_NOT_FOUND_ON_S3_BY_KEY_FOR_BUCKET.getMessageComposed(key, bucket));
        }
        S3Object s3Object = s3client.getObject(bucket, key);
        if (s3Object == null) {
            throw new EntityNotFoundException(DOCUMENT_NOT_FOUND_ON_S3_BY_KEY_FOR_BUCKET.getMessageComposed(key, bucket));
        }
        return s3Object;
    }


    public String generateDownloadLinkByObjectKey(String key) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key);
        request.setExpiration(DateUtil.addHoursToDate(new Date(), 1));
        URL url = s3client.generatePresignedUrl(request);
        return url != null ? url.toString() : null;
    }


    public void deleteByKey(String key) {
        s3client.deleteObject(bucket, key);
    }

    public PutObjectResult uploadDocumentFull(String key, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = getMetadataByFile(file);
            return s3client.putObject(bucket, key, inputStream, metadata);
        } catch (IOException e) {
            throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
        }
    }

    public void uploadImageAndThumbnailCompressed(String imageKey, String thumbnailKey, MultipartFile file) {
        try (ByteArrayOutputStream baosImgCompressed = new ByteArrayOutputStream();
             ByteArrayOutputStream baosThumbnailCompressed = new ByteArrayOutputStream();
             InputStream is = file.getInputStream()) {
            Thumbnails.of(is)
                    .scale(0.5)
                    .outputQuality(0.5)
                    .toOutputStream(baosImgCompressed);
            ByteArrayInputStream baisImgCompressed = new ByteArrayInputStream(baosImgCompressed.toByteArray());
            ByteArrayInputStream baisImgCompressedTmp = new ByteArrayInputStream(baosImgCompressed.toByteArray());
            Thumbnails.of(baisImgCompressedTmp)
                    .scale(0.5)
                    .outputQuality(0.5)
                    .dithering(Dithering.ENABLE)
                    .toOutputStream(baosThumbnailCompressed);
            ByteArrayInputStream baisImgCompressedThumbCompressed = new ByteArrayInputStream(baosThumbnailCompressed.toByteArray());
            ObjectMetadata metadata = getMetadataByFileNoContentLength(file);
            s3client.putObject(bucket, imageKey, baisImgCompressed, metadata);
            s3client.putObject(bucket, thumbnailKey, baisImgCompressedThumbCompressed, metadata);
        } catch (IOException ex) {
            throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), ex);
        }
//            try (ByteArrayInputStream baisImgTmp = new ByteArrayInputStream(baosImgCompressed.toByteArray());
//                 ByteArrayOutputStream baosImgCompressedTmp = new ByteArrayOutputStream();
//                 PipedInputStream baisImgCompressedThumbCompressed = new PipedInputStream();
//                 ByteArrayOutputStream baosImgCompressedThumbCompressedTmp = new ByteArrayOutputStream();
//                 PipedInputStream baisImgCompressedTmp = new PipedInputStream();
//                 PipedInputStream baisImgCompressed = new PipedInputStream()) {
//                baisImgTmp.transferTo(baosImgCompressedTmp);
//                new Thread(() -> {
//                    try (final PipedOutputStream out = new PipedOutputStream(baisImgCompressed)) {
//                        baosImgCompressed.writeTo(out);
//                    } catch (IOException e) {
//                        throw new InternalServerException("something went wrong", e);
//                    }
//                }).start();
//                new Thread(() -> {
//                    try (final PipedOutputStream out = new PipedOutputStream(baisImgCompressedTmp)) {
//                        baosImgCompressedTmp.writeTo(out);
//                    } catch (IOException e) {
//                        throw new InternalServerException("something went wrong", e);
//                    }
//                }).start();
//                Thumbnails.of(baisImgCompressedTmp)
//                        .scale(0.1)
//                        .outputQuality(0.1)
//                        .dithering(Dithering.ENABLE)
//                        .toOutputStream(baosImgCompressedThumbCompressedTmp);
//                new Thread(() -> {
//                    try (final PipedOutputStream out = new PipedOutputStream(baisImgCompressedThumbCompressed)) {
//                        baosImgCompressedThumbCompressedTmp.writeTo(out);
//                    } catch (IOException e) {
//                        throw new InternalServerException("something went wrong", e);
//                    }
//                }).start();
//                s3client.putObject(bucket, imageKey, baisImgCompressed, metadata);
//                s3client.putObject(bucket, thumbnailKey, baisImgCompressedThumbCompressed, metadata);
//            }
//        } catch (IOException e) {
//            throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
//        }
    }

    public String generatePostPictureKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, POST, IMAGE_TYPE, file);
    }

    public String generatePostPictureThumbnailKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, POST + "/thumbnail", IMAGE_TYPE, file);
    }

    public String generateGroupProfilePictureKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, GROUP, GROUP_PROFILE, IMAGE_TYPE, file);
    }

    public String generateUserProfilePictureKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, USER, USER_PROFILE, IMAGE_TYPE, file);
    }

    public String generateUserProfilePictureThumbnailKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, USER, USER_PROFILE + "/thumbnail", IMAGE_TYPE, file);
    }

    public String generateGroupProfilePictureThumbnailKey(Long id, MultipartFile file) {
        checkIfImage(file);
        return generateKey(id, GROUP, GROUP_PROFILE + "/thumbnail", IMAGE_TYPE, file);
    }

    private String generateKey(Long id, String prefix, String type, MultipartFile file) {
        return generateKey(id, prefix, null, type, file);
    }

    private String generateKey(Long id, String prefix, String postfix, String type, MultipartFile file) {
        return prefix + "/" + id + (postfix != null ? "/" + postfix : "/") + (type != null ? (type.trim() + "/") : "") + file.getOriginalFilename();
    }

    private void checkPostfix(@Nullable String postfix) {
        if (postfix == null) {
            return;
        }
        int length = postfix.trim().length();
        if (length == 0) {
            return;
        }
        char first = postfix.charAt(0);
        char last = postfix.charAt(length - 1);
        if (first != '/' || last != '/') {
            throw new InternalServerException("Invalid postfix", new Throwable());
        }
    }

    private ObjectMetadata getMetadataByFile(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }

    private ObjectMetadata getMetadataByFileNoContentLength(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        return metadata;
    }

    DocumentDto getDocument(ListObjectsV2Result result) {
        return DocumentDto
                .builder()
                .objectSummaries(
                        result
                                .getObjectSummaries()
                                .stream()
                                .map(
                                        c -> DocumentSummaryDto
                                                .builder()
                                                .downloadLink(generateDownloadLinkByObjectKey(c.getKey()))
                                                .bucketName(c.getBucketName())
                                                .key(c.getKey())
                                                .eTag(c.getETag())
                                                .size(c.getSize())
                                                .lastModified(c.getLastModified())
                                                .storageClass(c.getStorageClass())
                                                .owner(c.getOwner())
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .commonPrefixes(result.getCommonPrefixes())
                .isTruncated(result.isTruncated())
                .bucketName(result.getBucketName())
                .keyCount(result.getKeyCount())
                .nextContinuationToken(result.getNextContinuationToken())
                .prefix(result.getPrefix())
                .delimiter(result.getDelimiter())
                .maxKeys(result.getMaxKeys())
                .encodingType(result.getEncodingType())
                .continuationToken(result.getContinuationToken())
                .startAfter(result.getStartAfter())
                .build();
    }

    private ListObjectsV2Result getResult(String prefix, Long id) {
        return s3client.listObjectsV2(bucket, prefix + "/" + id + "/");
    }

    private ListObjectsV2Result getResult(String prefix, Long id, String type) {
        return getResult(prefix, id, type, null);
    }

    private ListObjectsV2Result getResult(String prefix, Long id, String type, String postfix) {
        checkPostfix(postfix);
        return s3client.listObjectsV2(bucket, prefix + "/" + id + (postfix != null ? postfix.trim() : "/") + (type != null ? (type.trim() + "/") : ""));
    }

    private void checkIfImage(MultipartFile file) {
        try {
            try (InputStream input = file.getInputStream()) {
                try {
                    ImageIO.read(input).toString();
                } catch (IOException e) {
                    throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
                } catch (Exception e) {
                    String NOT_IMAGE_EXCEPTION_MSG = "Uploaded file is not an image type: BMP, GIF, JPG or PNG";
                    throw new IllegalArgumentException(NOT_IMAGE_EXCEPTION_MSG);
                }
            }
        } catch (IOException e) {
            throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
        }
    }


}
