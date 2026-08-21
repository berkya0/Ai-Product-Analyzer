package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResult;
import com.berkaykomur.backend.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "analyses", ignore = true)
    Product toProduct(ScrapperResult scrapResult);


    @Mapping(target = "analyses", ignore = true)
    @Mapping(target = "id",ignore = true)
    void updateProductFromDto(ScrapperResult scrapResult, @MappingTarget Product product);

    ProductResponse toProductResponse(Product product);

}
