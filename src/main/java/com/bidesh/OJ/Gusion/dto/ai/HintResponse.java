package com.bidesh.OJ.Gusion.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HintResponse {
    private String level1;
    private String level2;
    private String level3;
}