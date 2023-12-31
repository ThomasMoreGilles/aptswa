package be.thomasmore.library.controllers;
import be.thomasmore.library.model.Movie;
import be.thomasmore.library.repositories.MovieRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MovieRepository movieRepository;


    @GetMapping({"", "/", "/home"})
    public String home() {
        return "admin/home";
    }

    @RequestMapping(value = {"/manageMovies","/managemovies"}, method = { RequestMethod.GET, RequestMethod.POST })
    public String manageMovies(Model model,
                            @RequestParam(required = false) String sortField,
                            @RequestParam(required = false) String sortOrder,
                            @RequestParam(required = false) String search) {

        Sort sort = Sort.by("id");  // Default sort

        if (sortField != null && !sortField.isEmpty()) {
            if ("desc".equalsIgnoreCase(sortOrder)) {
                sort = Sort.by(sortField).descending();
            } else {
                sort = Sort.by(sortField).ascending();
            }
        }

        Iterable<Movie> unarchivedMovies = movieRepository.findByArchivedFalse(sort);
        Iterable<Movie> archivedMovies = movieRepository.findByArchivedTrue(sort);

        model.addAttribute("unarchivedMovies", unarchivedMovies);
        model.addAttribute("archivedMovies", archivedMovies);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);

        return "admin/manageMovies";
    }

    @PostMapping({"/deleteMovie","/deletemovie"})
    public String deleteMovie(@RequestParam int id) {
        movieRepository.findById(id).ifPresent(movie -> movieRepository.delete(movie));
        return "redirect:/admin/manageMovies";
    }

    @GetMapping({"/addMovie","/addmovie"})
    public String addMovie(){
        return "admin/addMovie";
    }

    @PostMapping({"/addMovie","/addmovie"})
    public String addMoviePost(@RequestParam String title, @RequestParam String director,@RequestParam String description , @RequestParam int year, @RequestParam int duration){
        Movie movie = new Movie(director, title, description, duration, year);
        movieRepository.save(movie);
        return "redirect:/admin/manageMovies";
    }

    @PostMapping({"/archiveMovie","/archivemovie"})
    public String archiveMovie(@RequestParam int id) {
    movieRepository.findById(id).ifPresent(movie -> {
        movie.setArchived(true);
        movieRepository.save(movie);
    });
    return "redirect:/admin/manageMovies";
    }

    @PostMapping({"/unarchiveMovie","/unarchivemovie"})
    public String unarchiveMovie(@RequestParam int id) {
    movieRepository.findById(id).ifPresent(movie -> {
        movie.setArchived(false);
        movieRepository.save(movie);
    });
    return "redirect:/admin/manageMovies";
    }

    @GetMapping({"/editMovie","/editmovie"})
    public String editMovie(@RequestParam int id, Model model){
    Optional<Movie> movie = movieRepository.findById(id);
    movie.ifPresent(m -> model.addAttribute("movie", m));
    return "admin/editMovie";
    }

    @PostMapping({"/editMovie","/editmovie"})
    public String editMoviePost(@ModelAttribute Movie movie){
    movieRepository.save(movie);
    return "redirect:/admin/manageMovies";
    }

}