package com.example.loginapp.mapper;

import java.util.List;
import com.example.loginapp.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products")
    List<Product> findAll();
}