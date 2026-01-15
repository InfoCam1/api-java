package es.plaiaundi.infoCam.api_java.controller;

import es.plaiaundi.infoCam.api_java.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @Autowired private SyncService syncService;

    @GetMapping
    public String sync() {
        return syncService.sincronizarDatos();
    }
}
