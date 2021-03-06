package cse.maven_webmail.util;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.parser.CustomContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * 휴지통 파일을 포매팅하는 클래스입니다.
 */
public final class TrashMessageFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrashMessageFormatter.class);
    private String toAddress;
    private String fromAddress;
    private String subject;
    private String date;
    private String cc;
    private String body;
    private String fileName;
    private InputStream fileStream;
    private InputStream mailStream;

    public String getCc() {
        return cc;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setMailStream(InputStream mailStream) {
        this.mailStream = mailStream;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    /**
     * 파싱하는 함수입니다.
     */
    public void parse() {
        var contentHandler = new CustomContentHandler();

        MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
        BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();
        var mime4jParser = new MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder);
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(contentHandler);
        try {
            mime4jParser.parse(mailStream);
            var email = contentHandler.getEmail();
            toAddress = email.getToEmailHeaderValue();
            cc = email.getCCEmailHeaderValue();
            fromAddress = email.getFromEmailHeaderValue();
            subject = email.getEmailSubject();
            date = email.getHeader().getField("Date").getBody();
            Attachment plainTextEmailBody = email.getPlainTextEmailBody();
            body = new String(plainTextEmailBody.getIs().readAllBytes());
            List<Attachment> attachments = email.getAttachments();
            if (attachments.size() == 1) {
                    fileName = attachments.get(0).getAttachmentName();
                    fileStream = attachments.get(0).getIs();

            }
        } catch (MimeException | IOException e) {
            LOGGER.error("{} : {}",new Date(), e.getMessage());
        }
    }
}
