package com.turingSecApp.turingSec.model.entities.message;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
//@DiscriminatorValue("string")
@Table(name = "messageInReport_string")
public class StringMessageInReport extends BaseMessageInReport{
    @Column(nullable = false)
    private String content;
}
