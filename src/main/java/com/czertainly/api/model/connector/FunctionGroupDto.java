package com.czertainly.api.model.connector;

import com.czertainly.api.model.Identified;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FunctionGroupDto extends BaseFunctionGroupDto implements Identified {

    private Long id;
    private String uuid;
    private String name;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("functionGroupCode", functionGroupCode)
                .append("kinds", kinds)
                .append("endPoints", endPoints)
                .append("id", id)
                .append("uuid", uuid)
                .append("name", name)
                .toString();
    }
}
