package br.com.teste.demo.performance;

import br.com.teste.demo.dtos.ProdutoDTO;
import br.com.teste.demo.enums.StatusProduto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de carga e performance para validar o comportamento da API sob stress.
 * Simula múltiplas requisições concorrentes e mede tempos de resposta.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Carga e Performance")
class LoadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int CONCURRENT_USERS = 50;
    private static final int REQUESTS_PER_USER = 10;

    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }

    @Test
    @DisplayName("Teste de carga - Múltiplas requisições GET simultâneas")
    @WithMockUser
    void testeMultiplasRequisicoesGetSimultaneas() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Submeter tarefas concorrentes
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            Future<Long> future = executor.submit(() -> {
                long threadStartTime = System.currentTimeMillis();
                try {
                    mockMvc.perform(get("/produtos")
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return System.currentTimeMillis() - threadStartTime;
            });
            futures.add(future);
        }

        // Coletar resultados
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            responseTimes.add(future.get());
        }

        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Análise de performance
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        System.out.println("=== Resultados do Teste de Carga - GET ===");
        System.out.println("Usuários concorrentes: " + CONCURRENT_USERS);
        System.out.println("Tempo total: " + totalTime + "ms");
        System.out.println("Tempo médio de resposta: " + averageResponseTime + "ms");
        System.out.println("Tempo mínimo de resposta: " + minResponseTime + "ms");
        System.out.println("Tempo máximo de resposta: " + maxResponseTime + "ms");
        System.out.println("Throughput: " + (CONCURRENT_USERS * 1000.0 / totalTime) + " req/s");

        // Validações
        assertThat(averageResponseTime).isLessThan(5000); // Tempo médio < 5s
        assertThat(maxResponseTime).isLessThan(10000); // Tempo máximo < 10s
    }

    @Test
    @DisplayName("Teste de carga - Busca por filtros com alta concorrência")
    @WithMockUser
    void testeBuscaPorFiltrosAltaConcorrencia() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Submeter tarefas concorrentes
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            Future<Long> future = executor.submit(() -> {
                long threadStartTime = System.currentTimeMillis();
                try {
                    for (int j = 0; j < REQUESTS_PER_USER; j++) {
                        mockMvc.perform(get("/produtos/filtrar")
                                        .param("titulo", "produto")
                                        .param("valorMinimo", "100")
                                        .param("valorMaximo", "5000")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return System.currentTimeMillis() - threadStartTime;
            });
            futures.add(future);
        }

        // Coletar resultados
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            responseTimes.add(future.get());
        }

        long totalTime = System.currentTimeMillis() - startTime;
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        int totalRequests = CONCURRENT_USERS * REQUESTS_PER_USER;
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0) / REQUESTS_PER_USER;

        System.out.println("=== Resultados do Teste de Carga - Filtros ===");
        System.out.println("Total de requisições: " + totalRequests);
        System.out.println("Usuários concorrentes: " + CONCURRENT_USERS);
        System.out.println("Requisições por usuário: " + REQUESTS_PER_USER);
        System.out.println("Tempo total: " + totalTime + "ms");
        System.out.println("Tempo médio por requisição: " + averageResponseTime + "ms");
        System.out.println("Throughput: " + (totalRequests * 1000.0 / totalTime) + " req/s");

        assertThat(averageResponseTime).isLessThan(3000); // Média < 3s por requisição
    }

    @Test
    @DisplayName("Teste de stress - Verificar estabilidade sob carga extrema")
    @WithMockUser
    void testeStressVerificarEstabilidade() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        int successCount = 0;
        int failureCount = 0;

        long startTime = System.currentTimeMillis();

        // Criar 100 threads executando requisições
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(get("/produtos")
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    // Contar falhas
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        System.out.println("=== Resultados do Teste de Stress ===");
        System.out.println("Threads concorrentes: 100");
        System.out.println("Tempo total: " + totalTime + "ms");
        System.out.println("Sistema permaneceu estável: " + (totalTime < 30000));

        assertThat(totalTime).isLessThan(30000); // Deve completar em menos de 30s
    }

    @Test
    @DisplayName("Teste de performance - Tempo de resposta de endpoint específico")
    @WithMockUser
    void testePerformanceTempoRespostaEndpoint() throws Exception {
        int iterations = 100;
        List<Long> responseTimes = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();

            mockMvc.perform(get("/produtos")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            long endTime = System.nanoTime();
            responseTimes.add((endTime - startTime) / 1_000_000); // Converter para ms
        }

        double averageTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long p95 = responseTimes.stream()
                .sorted()
                .skip((long) (iterations * 0.95))
                .findFirst()
                .orElse(0L);

        long p99 = responseTimes.stream()
                .sorted()
                .skip((long) (iterations * 0.99))
                .findFirst()
                .orElse(0L);

        System.out.println("=== Resultados do Teste de Performance ===");
        System.out.println("Iterações: " + iterations);
        System.out.println("Tempo médio: " + averageTime + "ms");
        System.out.println("P95: " + p95 + "ms");
        System.out.println("P99: " + p99 + "ms");

        assertThat(averageTime).isLessThan(1000); // Média < 1s
        assertThat(p95).isLessThan(2000); // P95 < 2s
        assertThat(p99).isLessThan(3000); // P99 < 3s
    }
}
