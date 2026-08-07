package com.berkaykomur.backend.util;

import com.berkaykomur.backend.exception.UnspportedMarketPlaceException;
import com.berkaykomur.backend.scrapper.Scrapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ScrapUtil {
    private final List<Scrapper> scrappers;

}
