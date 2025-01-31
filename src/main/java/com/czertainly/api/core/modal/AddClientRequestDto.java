package com.czertainly.api.core.modal;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Class representing client registration request
 */
public class AddClientRequestDto {
	
	@Schema(
            description = "Base64 content of the client certificate",
            required = true
    )
    private String clientCertificate;
    
	@Schema(
            description = "UUID of the existing certificate in the Inventory",
            required = true
    )
    private String certificateUuid;
	
	@Schema(
            description = "Name of the client",
            required = true
    )
    private String name;
	
	@Schema(
            description = "Description of the Client",
            required = true
    )
    private String description;
	
	@Schema(
            description = "Is the client enabled?",
            required = true
    )
    private Boolean enabled;

    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCertificateUuid() {
		return certificateUuid;
	}

	public void setCertificateUuid(String certificateUuid) {
		this.certificateUuid = certificateUuid;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("clientCertificate", clientCertificate)
                .append("name", name)
                .append("description", description)
                .append("enabled", enabled)
                .append("certificateUuid", certificateUuid)
                .toString();
    }
}

