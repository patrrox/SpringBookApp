package ostasp.bookapp.catalog.application.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ostasp.bookapp.catalog.application.port.CatalogInitializerUseCase;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final CatalogInitializerUseCase initializerUseCase;


    @PostMapping("/initialization")
    @Transactional
    public void initialize() {
        initializerUseCase.initialize();
    }




}
