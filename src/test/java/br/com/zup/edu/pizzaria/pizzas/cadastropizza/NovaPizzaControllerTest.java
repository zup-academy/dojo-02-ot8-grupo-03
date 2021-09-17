package br.com.zup.edu.pizzaria.pizzas.cadastropizza;

import br.com.zup.edu.pizzaria.ingredientes.Ingrediente;
import br.com.zup.edu.pizzaria.ingredientes.IngredienteRepository;
import br.com.zup.edu.pizzaria.pedidos.TipoDeBorda;
import br.com.zup.edu.pizzaria.pedidos.novopedido.EnderecoRequest;
import br.com.zup.edu.pizzaria.pedidos.novopedido.ItemRequest;
import br.com.zup.edu.pizzaria.pedidos.novopedido.NovoPedidoRequest;
import br.com.zup.edu.pizzaria.pizzas.Pizza;
import br.com.zup.edu.pizzaria.pizzas.PizzaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.Assert;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class NovaPizzaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Test
    void deveCadastrarNovaPizza() throws Exception {

        List<Long> idsDosIngredientes = new ArrayList<>();

        Ingrediente ingrediente = new Ingrediente("Frango", 1, new BigDecimal("5.0"));
        ingrediente = ingredienteRepository.save(ingrediente);
        idsDosIngredientes.add(ingrediente.getId());

        ingrediente = new Ingrediente("Catupiry", 1, new BigDecimal("5.0"));
        ingrediente = ingredienteRepository.save(ingrediente);
        idsDosIngredientes.add(ingrediente.getId());

        NovaPizzaRequest body = new NovaPizzaRequest("Pizza de Franpiry", idsDosIngredientes);
        MockHttpServletRequestBuilder request = post("/api/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(redirectedUrlPattern("/api/pizzas/{id}"));

    }

    @Test
    void naoDeveCadastrarNovaPizzaRepetida() throws Exception {

        List<Long> idsDosIngredientes = new ArrayList<>();

        Ingrediente ingrediente = new Ingrediente("Frango", 1, new BigDecimal("5.0"));
        ingrediente = ingredienteRepository.save(ingrediente);
        idsDosIngredientes.add(ingrediente.getId());

        ingrediente = new Ingrediente("Catupiry", 1, new BigDecimal("5.0"));
        ingrediente = ingredienteRepository.save(ingrediente);
        idsDosIngredientes.add(ingrediente.getId());

        NovaPizzaRequest body = new NovaPizzaRequest("Pizza de Franpiry", idsDosIngredientes);
        /**
         * Pizza 01
         */
        MockHttpServletRequestBuilder request = post("/api/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(body));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(redirectedUrlPattern("/api/pizzas/{id}"));

        /**
         * Pizza 02
         */
        NovaPizzaRequest bodyDuplicado = new NovaPizzaRequest("Pizza de Franpiry", idsDosIngredientes);
        MockHttpServletRequestBuilder requestDuplicado = post("/api/pizzas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bodyDuplicado));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("O Valor já esta cadastrado");
    }

    @Test
    void naoDeveCadastrarPizzaSemIngredientes() throws Exception {

        Pizza pizza = new Pizza("Queijo", new ArrayList<Ingrediente>(), new BigDecimal(20));

        try {
            pizzaRepository.save(pizza);
        } catch (Exception e) {
            Assert.isTrue(e.getMessage().contains("tamanho deve ser entre 1 e"));
        }
    }

}