package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding document type information.
 */
@Entity
@Table(name = "document_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentType {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "description")
    private String description;

    @Column(name = "branch_code")
    private String branchCode;

    @OneToMany(mappedBy = "documentType")
    private Set<EmployeeDocument> employeeDocuments;

    @Column(name = "status")
    private Status status;
}
