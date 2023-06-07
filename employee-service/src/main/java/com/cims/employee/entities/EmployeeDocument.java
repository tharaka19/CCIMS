package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "employee_document")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDocument {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    @Column(name = "status")
    private Status status;
}
