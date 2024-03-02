package com.wixis360.verifiedcontractingbackend.controller;

import com.wixis360.verifiedcontractingbackend.dto.APIResponse;
import com.wixis360.verifiedcontractingbackend.dto.AbilityDto;
import com.wixis360.verifiedcontractingbackend.enums.APIResponseStatus;
import com.wixis360.verifiedcontractingbackend.service.AbilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/ability")
@AllArgsConstructor
public class AbilityController {
    private AbilityService abilityService;

    @GetMapping
    public ResponseEntity<APIResponse> getAll() {
        APIResponse responseDTO = APIResponse
                .<List<AbilityDto>>builder()
                .status(APIResponseStatus.SUCCESS.name())
                .results(abilityService.findAll())
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
