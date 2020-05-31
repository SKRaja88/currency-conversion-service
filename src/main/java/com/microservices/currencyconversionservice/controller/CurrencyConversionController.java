package com.microservices.currencyconversionservice.controller;

import com.microservices.currencyconversionservice.model.CurrencyConversion;
import com.microservices.currencyconversionservice.service.FeignClientProxyToCallCurrentExchangeFromCurrentConversion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private FeignClientProxyToCallCurrentExchangeFromCurrentConversion proxy;

    CurrencyConversionController(FeignClientProxyToCallCurrentExchangeFromCurrentConversion proxy){
        this.proxy=proxy;
    }

    @GetMapping("/currency-conversion-service-feignclient/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrencyFeignClient(@PathVariable String from, @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {

       CurrencyConversion response = proxy.retrieveLimitsFromConfigurations(from,to);

        return new CurrencyConversion(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    @GetMapping("/currency-conversion-service/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrency(@PathVariable String from, @PathVariable String to,
                                                  @PathVariable BigDecimal quantity) {

       Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8081/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class,
                uriVariables);

        CurrencyConversion response = responseEntity.getBody();

        return new CurrencyConversion(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }
}
