package hr.tvz.groops.dto.response;

import com.amazonaws.services.s3.model.Owner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DocumentSummaryDto {
    private String downloadLink;
    private String bucketName;
    private String key;
    private String eTag;
    private long size;
    private Date lastModified;
    private String storageClass;
    private Owner owner;
}

