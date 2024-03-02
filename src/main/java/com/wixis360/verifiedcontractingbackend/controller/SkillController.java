package com.wixis360.verifiedcontractingbackend.controller;

import com.wixis360.verifiedcontractingbackend.dto.APIResponse;
import com.wixis360.verifiedcontractingbackend.dto.SkillDto;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.service.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/skill")
@AllArgsConstructor
public class SkillController {
    private SkillService skillService;

    @GetMapping
    public ResponseEntity<APIResponse> getAll(@RequestParam String type) {
        APIResponse responseDTO = APIResponse
                .<List<SkillDto>>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(skillService.findAllByType(type))
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
