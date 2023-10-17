package com.reactiveprogramming;

import com.reactiveprogramming.controller.ProductController;
import com.reactiveprogramming.dto.ProductDto;
import com.reactiveprogramming.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebFluxTest(ProductController.class)
class SpringReactiveMongoCrudApplicationTests {

    @MockBean
    ProductService productService;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testAddProduct() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("1", "Mobile", "1", 15000d));
        when(productService.saveProduct(productDtoMono)).thenReturn(productDtoMono);
        webTestClient.post().uri("/products/save")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetAllProducts() {

        Flux<ProductDto> productDtoFlux = Flux.just(new ProductDto("101", "Mobile", "1", 15000d),
                new ProductDto("102", "Charger", "1", 500d));

        when(productService.getProducts()).thenReturn(productDtoFlux);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products/productsList")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(new ProductDto("101", "Mobile", "1", 15000d))
                .expectNext(new ProductDto("102", "Charger", "1", 500d))
                .verifyComplete();
    }

    @Test
    public void testGetProductById() {
        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("201", "Mobile", "1", 15000d));
        when(productService.getProduct("201")).thenReturn(productDtoMono);

        Flux<ProductDto> responseBody = webTestClient.get().uri("/products/201")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNextMatches(p -> p.getName().equals("Mobile"))
                .verifyComplete();

    }

    @Test
    public void testUpdateProduct() {

        Mono<ProductDto> productDtoMono = Mono.just(new ProductDto("201", "Mobile", "1", 15000d));
        when(productService.updateProduct(productDtoMono, "201")).thenReturn(productDtoMono);

        webTestClient.put().uri("/products/update/201")
                .body(Mono.just(productDtoMono), ProductDto.class)
                .exchange()
                .expectStatus().isOk();

    }
    
}
