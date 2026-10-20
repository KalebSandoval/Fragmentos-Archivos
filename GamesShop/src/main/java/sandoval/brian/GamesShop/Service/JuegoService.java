package sandoval.brian.GamesShop.Service;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sandoval.brian.GamesShop.Models.Juego;
import sandoval.brian.GamesShop.Repository.JuegoRepository;

import java.util.List;

@Service
public class JuegoService {
    @Autowired
    private JuegoRepository juegoRepository;

    public List<Juego> listarTodos(){
        return juegoRepository.findAll();
    }

    public void guardarJuego(Juego juego, MultipartFile portada){
        String urlImagen = "default.png";

        if (!portada.isEmpty()){

        }

        juego.setPortadaUrl(urlImagen);

        juegoRepository.save(juego);
    }

    public List<Juego> buscarPorTitulo(String palabra){
        if (palabra ==null ||palabra.trim().isEmpty()){
            return listarTodos();
        }
        return juegoRepository.findByTituloContainingIgnoreCase(palabra);
    }

}
