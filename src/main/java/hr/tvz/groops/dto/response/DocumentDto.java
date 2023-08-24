package hr.tvz.groops.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DocumentDto {
    private List<DocumentSummaryDto> objectSummaries = new ArrayList<>();
    private List<String> commonPrefixes = new ArrayList<String>();
    private boolean isTruncated;
    private String bucketName;
    private int keyCount;
    private String nextContinuationToken;
    private String prefix;
    private String delimiter;
    private int maxKeys;
    private String encodingType;
    private String continuationToken;
    private String startAfter;
}
