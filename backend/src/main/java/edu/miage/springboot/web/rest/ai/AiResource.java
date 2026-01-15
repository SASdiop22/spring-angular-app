package edu.miage.springboot.web.rest.ai;

import edu.miage.springboot.web.dtos.ai.MatchingResultDTO;
import edu.miage.springboot.services.interfaces.AiMatchingService;
import edu.miage.springboot.services.interfaces.CvTextExtractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class AiResource {

    private final CvTextExtractorService cvService;
    private final AiMatchingService aiService;

    public AiResource(CvTextExtractorService cvService,
                      AiMatchingService aiService) {
        this.cvService = cvService;
        this.aiService = aiService;
    }

    @PostMapping("/match")
    public ResponseEntity<MatchingResultDTO> matchCv(
            @RequestParam("cv") MultipartFile cv,
            @RequestParam("job") String jobDescription
    ) {
        // 1️⃣ Extraction texte CV
        String cvText = cvService.extractText(cv);

        // 2️⃣ Analyse IA → DTO
        MatchingResultDTO result =
                aiService.matchCvWithJob(cvText, jobDescription);

        // 3️⃣ Retour JSON propre
        return ResponseEntity.ok(result);
    }
}
