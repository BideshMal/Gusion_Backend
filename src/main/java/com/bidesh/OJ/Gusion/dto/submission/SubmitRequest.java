package com.bidesh.OJ.Gusion.dto.submission;

import com.bidesh.OJ.Gusion.entity.Language;
import lombok.Data;

@Data
public class SubmitRequest {
    
    private Long problemId;

    // ✅ TRICK: Accept ANY string to stop the 400 Error
    private String lang;      
    private String language;  

    private String code;
    private String customInput;

    /**
     * Helper method to convert String -> Enum safely.
     * Use this in your Controllers/Services instead of getLang()!
     */
    public Language getRealLanguage() {
        // 1. Pick whichever field is not null
        String target = (lang != null) ? lang : language;
        
        // 2. Default to JAVA if everything is missing
        if (target == null) {
            return Language.JAVA; 
        }

        // 3. Force Uppercase to handle "Java" -> "JAVA"
        try {
            return Language.valueOf(target.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Language.JAVA; // Fallback
        }
    }
}