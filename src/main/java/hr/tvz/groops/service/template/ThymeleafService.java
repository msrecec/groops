package hr.tvz.groops.service.template;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class ThymeleafService implements TemplateService {
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafService.class);
    private final SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    public ThymeleafService(SpringTemplateEngine thymeleafTemplateEngine) {
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    @Override
    public String generateVerificationTemplateHtml(@NotNull String templateHtml, @NotNull String verificationPredicate, @NotNull String linkPrefix, @NotNull String queryParam, @NotNull String b64Token) {
        logger.debug("Generating template");
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("verificationPredicate", verificationPredicate);
        String link = linkPrefix + "?" + queryParam + "=" + b64Token;
        thymeleafContext.setVariable("link", link);
        return thymeleafTemplateEngine.process(templateHtml, thymeleafContext);
    }
}
