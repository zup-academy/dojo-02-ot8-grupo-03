package br.com.zup.edu.pizzaria.pedidos.novopedido;

import br.com.zup.edu.pizzaria.ingredientes.Ingrediente;
import br.com.zup.edu.pizzaria.pedidos.TipoDeBorda;
import br.com.zup.edu.pizzaria.pizzas.Pizza;
import br.com.zup.edu.pizzaria.pizzas.PizzaRepository;
import br.com.zup.edu.pizzaria.pizzas.cadastropizza.NovaPizzaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
class NovoPedidoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PizzaRepository pizzaRepository;

    @Test
    void deveCadastrarNovoPedido() throws Exception {

        Ingrediente ingrediente = new Ingrediente("Queijo", 1, new BigDecimal("5.0"));
        List<Ingrediente> ingredienteList = new ArrayList<>();
        ingredienteList.add(ingrediente);
        Pizza pizza = new Pizza("Queijo", ingredienteList);
        pizzaRepository.save(pizza);

        EnderecoRequest enderecoRequest = new EnderecoRequest("Rua abc", "123", "Muro branco", "12345678");
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(new ItemRequest(pizza.getId(), TipoDeBorda.TRADICIONAL));
        NovoPedidoRequest novoPedidoRequest = new NovoPedidoRequest(enderecoRequest, itemRequestList);

        MockHttpServletRequestBuilder request = post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(novoPedidoRequest));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(redirectedUrlPattern("/api/pedidos/{id}"));

    }

}