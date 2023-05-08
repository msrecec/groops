package hr.tvz.groops.service.template;

import org.jetbrains.annotations.NotNull;

public interface TemplateService {
    String generateVerificationTemplateHtml(@NotNull String templateHtml, @NotNull String verificationPredicate, @NotNull String linkPrefix, @NotNull String queryParam, @NotNull String b64Token);
}
