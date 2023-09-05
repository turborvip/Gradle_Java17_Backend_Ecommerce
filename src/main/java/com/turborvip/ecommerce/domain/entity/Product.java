package com.turborvip.ecommerce.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.turborvip.ecommerce.application.constants.ProductType;
import com.turborvip.ecommerce.domain.entity.base.AbstractBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

//@Entity
@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
//@Table(name = "products",schema = "product")
public class Product extends AbstractBase {

    @Column(name = "product_name")
    @NotEmpty(message = "Product name not be empty!")
    private String name;

    @Column(name = "product_thumbnail")
    private String thumbnail;

    @Column(name = "product_description")
    private String description;

    @Column(name = "product_price")
    private Number price;

    @Column(name = "product_quantity")
    private int quantity;

    @Column(name = "product_type")
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn
    private User shop;

    @Column(columnDefinition = "jsonb")
    private String attribute;
}
