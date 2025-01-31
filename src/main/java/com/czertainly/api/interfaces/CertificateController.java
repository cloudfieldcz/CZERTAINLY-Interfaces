package com.czertainly.api.interfaces;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.model.ca.CertRevocationDto;
import com.czertainly.api.model.ca.CertificateSignRequestDto;
import com.czertainly.api.model.ca.CertificateSignResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/caConnector/authorities/{authorityId}/endEntityProfiles/{endEntityProfileName}/certificates")
@Tag(name = "Certificate Management API", description = "Certificate Management API")
public interface CertificateController {

    @Operation(
            summary = "Issue certificate",
            description = "Method for issuing certificates " +
                    "based on given request."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate issued"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content
                    )
            })
    @RequestMapping(path = "/issue", method = RequestMethod.POST)
    CertificateSignResponseDto issueCertificate(
            @PathVariable Long authorityId,
            @PathVariable String endEntityProfileName,
            @RequestBody CertificateSignRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Revoke certificate",
            description = "Method for revoking existing certificates " +
                    "by given request."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate revoked"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content
                    )
            })
    @RequestMapping(path = "/revoke", method = RequestMethod.POST)
    void revokeCertificate(
            @PathVariable Long authorityId,
            @PathVariable String endEntityProfileName,
            @RequestBody CertRevocationDto request) throws NotFoundException;

}