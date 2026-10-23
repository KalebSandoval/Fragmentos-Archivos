package sandoval.brian.GamesShop.service;

import org.springframework.util.StringUtils;
import sandoval.brian.GamesShop.models.Juego;
import sandoval.brian.GamesShop.repository.JuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class JuegoService {

    @Autowired
    private JuegoRepository juegoRepository;

    public List<Juego> listarTodos(){
        return juegoRepository.findAll();
    }

    public void guardarJuego(Juego juego, MultipartFile portada) {
        String urlImagen = "default.png";

        if (portada != null && !portada.isEmpty()) {
            try {
                Path uploadPath = Paths.get("src/main/resources/static/uploads/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String original = StringUtils.cleanPath(
                        portada.getOriginalFilename() == null ? "imagen" : portada.getOriginalFilename());
                original = original.replaceAll("[^a-zA-Z0-9._-]", "_");
                urlImagen = UUID.randomUUID() + "_" + original;
                Files.copy(portada.getInputStream(), uploadPath.resolve(urlImagen));
            } catch (IOException e) {
                e.printStackTrace();   // aquí manejas la excepción
            }
        }

        juego.setPortadaUrl(urlImagen);
        juegoRepository.save(juego);
    }

    public List<Juego> buscarPorTitulo(String palabra){
        if(palabra == null || palabra.trim().isEmpty()){
            return listarTodos();
        }
        return juegoRepository.findByTituloContainingIgnoreCase(palabra);
    }

    public Juego obtenerJuegoPorId(Long id){
        return juegoRepository.findById(id).orElse(null);
    }

    public void eliminarJuego(Long id){
        juegoRepository.deleteById(id);
    }
}
