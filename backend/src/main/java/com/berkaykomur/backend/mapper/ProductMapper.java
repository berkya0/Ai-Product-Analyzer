package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResponse;
import com.berkaykomur.backend.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analyses", ignore = true)
    Product toProduct(ScrapperResponse scrapResult);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "analyses", ignore = true)
    void updateProductFromDto(ScrapperResponse scrapResult, @MappingTarget Product product);

    ProductResponse toProductResponse(Product product);

}
