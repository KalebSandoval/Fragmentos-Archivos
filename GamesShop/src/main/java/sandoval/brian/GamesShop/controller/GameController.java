package sandoval.brian.GamesShop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import sandoval.brian.GamesShop.models.Juego;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sandoval.brian.GamesShop.service.JuegoService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class GameController {


    private static final List<Juego> juegos = new ArrayList<>();
    private static long idCounter = 1;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private JuegoService juegoService;

    static {
        juegos.add(new Juego(idCounter++, "The Legend of Zelda", "Juego de aventura", "zelda.jpg", 800.00, "Aventura"));
    }

    @GetMapping("/fragments-demo")
    public String fragmentsDemo() {
        return "fragments-demo";
    }

    @GetMapping({"/", "/juegos"})
    public String listarJuegos(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("juegos", juegoService.buscarPorTitulo(buscar));
            model.addAttribute("busquedaActual", buscar);
        }
        model.addAttribute("juegos", juegoService.listarTodos());
        return "juegos";
    }

    @GetMapping("/juegos/nuevo")
    public String mostrarFormulario() {
        return "formulario";
    }

    @PostMapping("/juegos")
    public String guardarJuego(@RequestParam("titulo") String titulo,
                               @RequestParam("descripcion") String descripcion,
                               @RequestParam("precio") Double precio,
                               @RequestParam("categoria") String categoria,
                               @RequestParam("portada") MultipartFile portada) {

        String nombreArchivo = "default.png";

        Juego juegoNuevo = new Juego(null, titulo, descripcion, nombreArchivo, precio, categoria);
        juegoService.guardarJuego(juegoNuevo, portada);

        return "redirect:/juegos";
    }

    @GetMapping("/juegos/editar/{id}")
    public String editarJuego(@PathVariable Long id, Model model) {
        Juego juego = juegoService.obtenerJuegoPorId(id);

        if (juego != null) {
            model.addAttribute("juego", juego);
            return "formulario";
        }
        return "juegos";
    }

    @PostMapping("/juegos/editar/{id}")
    public String actualizarJuego(@PathVariable Long id,
                                  @RequestParam("titulo") String titulo,
                                  @RequestParam("descripcion") String descripcion,
                                  @RequestParam("precio") Double precio,
                                  @RequestParam("categoria") String categoria,
                                  @RequestParam(value = "portada", required = false) MultipartFile portada) {
        Juego juegoExistente = juegoService.obtenerJuegoPorId(id);

        //Ese bloque guarda en disco la imagen que el usuario subió en el formulario de edición y registra su nombre en el juego
        if (juegoExistente != null) {
            juegoExistente.setTitulo(titulo);
            juegoExistente.setDescripcion(descripcion);
            juegoExistente.setPrecio(precio);
            juegoExistente.setCategoria(categoria);
            if (portada != null && !portada.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    String original = StringUtils.cleanPath(
                            portada.getOriginalFilename() == null ? "imagen" : portada.getOriginalFilename());
                    original = original.replaceAll("[^a-zA-Z0-9._-]", "_");
                    String nombreArchivo = UUID.randomUUID() + "_" + original;
                    Files.copy(portada.getInputStream(), uploadPath.resolve(nombreArchivo));
                    juegoExistente.setPortadaUrl(nombreArchivo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            juegoService.actualizarJuego(juegoExistente);
        }
        return "redirect:/juegos";
    }

    @GetMapping("/juego/{id}")
    public String detalleJuego(@PathVariable Long id, Model model) {
        Juego juego = juegoService.obtenerJuegoPorId(id);
        //Por si no existe el juego te mandaría a una página de error, pero en este caso te regresa al inicio.
        if (juego == null) {
            return "redirect:/juegos";
        }
        model.addAttribute("juego", juego);


        return "detalle-juego";
    }


    @PostMapping("/juegos/eliminar/{id}")
    public String eliminarJuego(@PathVariable Long id) {
        Juego juego = juegoService.obtenerJuegoPorId(id);

        if (juego != null) {
            juegoService.eliminarJuego(id);
        }
        return "redirect:/juegos";
    }
}
