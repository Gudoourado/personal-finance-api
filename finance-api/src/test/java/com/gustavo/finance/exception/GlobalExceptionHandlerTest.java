package com.gustavo.finance.exception;

import com.gustavo.finance.controller.TransactionController;
import com.gustavo.finance.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class GlobalExceptionHandlerTest {

    private static final String TRANSACAO_VALIDA =
            "{\"description\":\"Mercado\",\"amount\":250.40,\"type\":\"DESPESA\",\"date\":\"2026-09-28\",\"categoryId\":1}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void jsonMalFormadoDevolve400() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content("{\"amount\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Corpo da requisição inválido"));
    }

    @Test
    void tipoInexistenteNoCorpoDevolve400() throws Exception {
        String transacao = TRANSACAO_VALIDA.replace("DESPESA", "DESPESAX");

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(transacao))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Corpo da requisição inválido"));
    }

    @Test
    void idComLetraDevolve400() throws Exception {
        mockMvc.perform(get("/api/transactions/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.message").value(containsString("'id'")));
    }

    @Test
    void dataEmFormatoErradoDevolve400() throws Exception {
        mockMvc.perform(get("/api/transactions/date-range")
                        .param("startDate", "28/09/2026")
                        .param("endDate", "2026-09-30"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("'startDate'")));
    }

    @Test
    void parametroObrigatorioAusenteDevolve400() throws Exception {
        mockMvc.perform(get("/api/transactions/date-range").param("startDate", "2026-09-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parâmetro obrigatório ausente"))
                .andExpect(jsonPath("$.message").value(containsString("'endDate'")));
    }

    @Test
    void metodoNaoSuportadoDevolve405() throws Exception {
        mockMvc.perform(patch("/api/transactions"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("Método não permitido"));
    }

    @Test
    void corpoQueNaoEJsonDevolve415() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.TEXT_PLAIN).content("oi"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Tipo de conteúdo não suportado"));
    }

    @Test
    void enderecoInexistenteDevolve404() throws Exception {
        mockMvc.perform(get("/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Endereço não encontrado"));
    }

    @Test
    void categoriaInexistenteDevolve404() throws Exception {
        when(transactionService.create(any())).thenThrow(new ResourceNotFoundException("Categoria não encontrada com id: 1"));

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(TRANSACAO_VALIDA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Categoria não encontrada com id: 1"));
    }

    @Test
    void camposInvalidosDevolvem400ComCadaCampo() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de validação"))
                .andExpect(jsonPath("$.fields.amount").exists())
                .andExpect(jsonPath("$.fields.categoryId").exists());
    }

    @Test
    void erroInesperadoDevolve500SemExporDetalheInterno() throws Exception {
        when(transactionService.findAll()).thenThrow(new IllegalStateException("senha do banco: s3gredo"));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.message").value(not(containsString("s3gredo"))));
    }
}
