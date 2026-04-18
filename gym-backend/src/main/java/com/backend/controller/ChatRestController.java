package com.backend.controller;

import java.util.List;
import com.backend.domain.MensajeChat;
import com.backend.repository.MensajeChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// --- REST CONTROLLER (Historial) ---
// Ponlo en un archivo separado o fuera de la clase anterior
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Para que Android no te dé problemas de CORS
class ChatRestController {

    
    @Autowired
    private MensajeChatRepository mensajeRepository;


    
   // --- REST: Historial con Paginación ---
    @GetMapping("/historial")
    public List<MensajeChat> getHistorial(
            @PageableDefault(size = 50) Pageable pageable) {
        
        // Trae los últimos 50 mensajes de forma eficiente
        Page<MensajeChat> pagina = mensajeRepository.findAllByOrderByFechaEnvioDesc(pageable);
        
        //  Al usar Descendente para traer los últimos, 
        // la lista viene al revés. En Android le daras la vuelta .
        return pagina.getContent(); 
    }
}