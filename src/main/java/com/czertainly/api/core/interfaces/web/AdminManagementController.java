package com.czertainly.api.core.interfaces.web;

import java.security.cert.CertificateException;
import java.util.List;

import com.czertainly.api.core.modal.AddAdminRequestDto;
import com.czertainly.api.core.modal.AdminDto;
import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.czertainly.api.core.modal.EditAdminRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/admins")
@Tag(name = "Admin Management API", description = "Admin Management API. "
		+ "Provides to Information regarding the Administrators in the platform "
		+ "With the Admin API, operations like addition, removal, enable etc... operations can be performed ")
public interface AdminManagementController {

	@Operation(summary = "List available administrators")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of administrator"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(method = RequestMethod.GET, produces = {"application/json"})
	public List<AdminDto> listAdmins();

	@Operation(summary = "Create a new Administrators")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "New administrator created"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(schema = @Schema(implementation = ValidationException.class))), })
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public ResponseEntity<?> addAdmin(@RequestBody AddAdminRequestDto request)
			throws CertificateException, AlreadyExistException, ValidationException, NotFoundException;

	@Operation(summary = "Get details of an Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Administrator details retrieved"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.GET, produces = {"application/json"})
	public AdminDto getAdmin(@PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Edit Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Administrator edit success"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
	public AdminDto editAdmin(@PathVariable String uuid, @RequestBody EditAdminRequestDto request)
			throws CertificateException, NotFoundException, AlreadyExistException;

	@Operation(summary = "Remove Multiple Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Administrator removed"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void bulkRemoveAdmin(@RequestBody List<String> adminUuids) throws NotFoundException;

	@Operation(summary = "Remove Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Administrator removed"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeAdmin(@PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Disable Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Disable administrator success"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}/disable", method = RequestMethod.PUT, consumes = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void disableAdmin(@PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Enable Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Enable administrator success"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/{uuid}/enable", method = RequestMethod.PUT, consumes = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void enableAdmin(@PathVariable String uuid) throws NotFoundException, CertificateException;

	@Operation(summary = "Disable Multiple Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Disable administrator success"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/disable", method = RequestMethod.PUT, consumes = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void bulkDisableAdmin(@RequestBody List<String> adminUuids) throws NotFoundException;

	@Operation(summary = "Enable Multiple Administrator")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Enable administrator success"),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content) })
	@RequestMapping(path = "/enable", method = RequestMethod.PUT, consumes = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void bulkEnableAdmin(@RequestBody List<String> adminUuids) throws NotFoundException;
}
