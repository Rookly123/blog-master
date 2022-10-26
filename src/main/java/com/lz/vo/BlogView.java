package com.lz.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.annotation.HandlesTypes;

@Data
@EqualsAndHashCode
public class BlogView {
    Long id;
    Integer views;
}
