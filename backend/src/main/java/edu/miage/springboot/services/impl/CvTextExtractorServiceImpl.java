package edu.miage.springboot.services.impl;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import edu.miage.springboot.services.interfaces.CvTextExtractorService;
import java.io.InputStream;

@Service
public class CvTextExtractorServiceImpl implements CvTextExtractorService {

    public String extractText(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Tika tika = new Tika();
            String text = tika.parseToString(inputStream);

            return  text
                    .replaceAll("\\s+", " ")
                    .replaceAll("[^\\p{L}\\p{N}.,;:/()\\-]", " ")
                    .trim();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'extraction du CV", e);
        }
    }
}
