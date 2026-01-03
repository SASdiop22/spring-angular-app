
package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.ai.*;

public interface AiMatchingService {

    MatchingResultDTO matchCvWithJob(String cvText, String jobDescription);
}
