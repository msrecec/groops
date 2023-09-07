package hr.tvz.groops.util;

import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class UploadUtil {
    public static void checkIfImage(MultipartFile file) {
        try {
            try (InputStream input = file.getInputStream()) {
                try {
                    ImageIO.read(input).toString();
                } catch (IOException e) {
                    throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Not an image");
                }
            }
        } catch (IOException e) {
            throw new InternalServerException(ExceptionEnum.IO_EXCEPTION.getFullMessage(), e);
        }
    }
}
