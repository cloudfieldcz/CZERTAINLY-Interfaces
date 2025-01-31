package com.czertainly.api.core.interfaces.web;

import java.util.List;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.model.discovery.CertificateGroupDto;
import com.czertainly.api.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/certificate-group")
@Tag(name = "Certificate Group API", description = "Certificate Group API")
public interface CertificateGroupController {
	@Operation(summary = "List of All Groups")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "list of available groups"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
	public List<CertificateGroupDto> listCertificateGroups();
	
	@Operation(summary = "Detail of an Group")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Group Details retrieved"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.GET, produces = {"application/json"})
	public CertificateGroupDto getCertificateGroup(@PathVariable String uuid) throws NotFoundException;
	
	@Operation(summary = "Create a new Group")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "New Group Created"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<?> createCertificateGroup(@RequestBody CertificateGroupDto request)
			throws AlreadyExistException, NotFoundException;
	
	@Operation(summary = "Update an Group")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Group updated"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public CertificateGroupDto updateCertificateGroup(@PathVariable String uuid, @RequestBody CertificateGroupDto request)
			throws NotFoundException;
	
	@Operation(summary = "Remove an Group")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Group removed"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeCertificateGroup(@PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Remove Multiple Group")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Groups removed"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void bulkRemoveCertificateGroup(@RequestBody List<String> groupUuids) throws NotFoundException;

}
