package com.ufscar.formularoute.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufscar.formularoute.dto.Lap;
import com.ufscar.formularoute.dto.Parameter;
import com.ufscar.formularoute.dto.ParameterResponse;
import com.ufscar.formularoute.dto.RegisterParametersRequest;
import com.ufscar.formularoute.repository.LapRepository;
import com.ufscar.formularoute.request.LapRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/main")
public class MainController {

        @Autowired
    private LapRepository lapRepository;

    /**
     * Endpoint para registrar parâmetros de telemetria associados a uma volta (Lap) existente.
     * <p>
     * Este endpoint recebe um JSON contendo o nome da volta (`lap-name`) e vários parâmetros
     * de telemetria (como `temperature`, `speed`, `pressure`, etc.), os quais serão convertidos
     * em objetos `Parameter` e adicionados à volta correspondente.
     * <p>
     * Funcionamento:
     * 1. O JSON enviado deve conter, obrigatoriamente, o campo `lap-name` que identifica a volta
     * no sistema. Além disso, pode conter diversos outros campos que representam parâmetros de
     * telemetria.
     * <p>
     * 2. O sistema faz a busca da volta (`Lap`) no banco de dados com base no nome informado em
     * `lap-name`.
     * <p>
     * 3. Para cada chave-valor do JSON (exceto `lap-name`), é criado um objeto `Parameter`, onde
     * a chave (key) será o nome do parâmetro e o valor (value) será o valor de telemetria
     * correspondente. O timestamp (`added`) é gerado automaticamente no momento da inserção.
     * <p>
     * 4. Todos os parâmetros são então adicionados à lista de parâmetros da volta encontrada, e
     * a volta é salva novamente no banco de dados com os novos dados.
     * <p>
     * 5. Se a volta não for encontrada no banco de dados, é retornada uma mensagem de erro com
     * status 404 (Not Found). Caso o JSON esteja mal formatado ou não contenha o campo
     * `lap-name`, uma resposta de erro 400 (Bad Request) é enviada.
     * <p>
     * Exemplo de JSON de entrada:
     * {
     * "lap-name": "Volta 1",
     * "temperature": "75",
     * "speed": "120",
     * "pressure": "30"
     * }
     * <p>
     * Respostas possíveis:
     * - 200 OK: Se a volta foi encontrada e os parâmetros foram registrados com sucesso.
     * Corpo da resposta: "Lap and parameters registered successfully."
     * <p>
     * - 404 Not Found: Se a volta com o nome fornecido não foi encontrada no banco de dados.
     * Corpo da resposta: "Lap not found."
     * <p>
     * - 400 Bad Request: Se o campo `lap-name` estiver ausente no JSON.
     * Corpo da resposta: "Missing lap-name in the request."
     * <p>
     * - 500 Internal Server Error: Se ocorrer algum erro durante o processamento da solicitação.
     *
     *
     * @return ResponseEntity com o status da operação (sucesso ou erro).
     */

// Em MainController.java

    @PostMapping("/register")
    @Transactional // Adiciona transacionalidade para garantir a consistência da operação
    public ResponseEntity<String> registerLap(@RequestBody RegisterParametersRequest request) {
        try {
            Optional<Lap> lapOptional = lapRepository.findByName(request.getLapName());

            if (lapOptional.isPresent()) {
                Lap lap = lapOptional.get();

                // O mapeamento agora é muito mais simples e seguro
                List<Parameter> newParameters = request.getParameters().entrySet().stream()
                        .map(entry -> {
                            Parameter parameter = new Parameter();
                            parameter.setKey(entry.getKey());
                            parameter.setValue(entry.getValue());
                            // Define a referência da volta no parâmetro
                            parameter.setLap(lap);
                            // O timestamp já é definido por padrão na entidade Parameter
                            return parameter;
                        })
                        .collect(Collectors.toList());

                // Adiciona os novos parâmetros à lista existente na volta
                lap.getParameters().addAll(newParameters);

                // Salva a volta (o CascadeType.ALL cuidará de salvar os novos parâmetros)
                lapRepository.save(lap);

                return ResponseEntity.ok("Lap and parameters registered successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lap not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    /**
     * Endpoint para criar uma nova volta (Lap) com um nome fornecido.
     *
     * Este endpoint recebe um JSON com o nome da volta (`lap-name`) e cria uma nova instância
     * de volta com esse nome, salvando-a no banco de dados.
     *
     * Exemplo de requisição:
     * POST /laps/create
     * Corpo da requisição:
     * {
     *   "lap-name": "Volta 1"
     * }
     *
     * Respostas possíveis:
     * - 201 Created: Se a volta foi criada com sucesso.
     *   Corpo da resposta: JSON com os detalhes da nova volta criada.
     *
     * - 400 Bad Request: Se o nome da volta não for fornecido ou for inválido.
     *   Corpo da resposta: "Lap name is required."
     *
     * - 500 Internal Server Error: Se ocorrer algum erro durante o processamento da solicitação.
     *
     * @return ResponseEntity contendo os detalhes da volta criada ou uma mensagem de erro.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createLap(@RequestBody LapRequest lapRequest) {
        try {
            // Verifica se o nome da volta foi fornecido
            if (lapRequest.getName() == null || lapRequest.getName().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lap name is required.");
            }

            if(lapRepository.findByName(lapRequest.getName()).isPresent()){
                System.out.println("Ja existe lap com esse nome");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lap name already exist.");
            }

            // Cria uma nova instância de Lap
            Lap newLap = new Lap();
            newLap.setName(lapRequest.getName());

            // Salva a nova volta no banco de dados
            Lap savedLap = lapRepository.save(newLap);

            // Retorna a volta criada com um status 201 Created
            System.out.println("Lap criada: " + savedLap.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    /**
     * Endpoint para buscar os valores de um parâmetro específico dentro de uma volta (Lap).
     * <p>
     * Este endpoint recebe o nome da volta (`lap-name`) e o nome do parâmetro (`key`) e
     * retorna uma lista de valores do parâmetro solicitado, se a volta e o parâmetro forem encontrados.
     * <p>
     * Exemplo de URL:
     * GET /laps/Volta%201/parameter/temperature <--- Formatação de URI no lap-name
     * <p>
     * Respostas possíveis:
     * - 200 OK: Se a volta e o parâmetro foram encontrados.
     * Corpo da resposta:
     * <p>
     * [
     * {
     * "key": "temperature",
     * "value": "75",
     * "added": "2023-09-21T15:30:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
     * },
     * {
     * "key": "temperature",
     * "value": "78",
     * "added": "2023-09-21T15:35:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
     * },
     * {
     * "key": "temperature",
     * "value": "80",
     * "added": "2023-09-21T15:40:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
     * }
     * ]
     * <p>
     * - 404 Not Found: Se a volta ou o parâmetro não forem encontrados.
     * Corpo da resposta: "Lap not found" ou "Parameter with key [key] not found in this lap".
     * <p>
     * - 500 Internal Server Error: Se ocorrer algum erro durante o processamento da solicitação.
     *
     * @param lapName O nome da volta (Lap) no qual os parâmetros serão pesquisados.
     * @param key     O nome do parâmetro (Key) cujos valores serão retornados.
     * @return ResponseEntity contendo os valores do parâmetro ou uma mensagem de erro.
     */
    @GetMapping("/{lapName}/parameter/{key}")
    public ResponseEntity<?> getParameterValues(@PathVariable String lapName, @PathVariable String key) {
        try {
            // Busca a volta (Lap) pelo nome
            Optional<Lap> lapOptional = lapRepository.findByName(lapName);

            if (lapOptional.isPresent()) {
                Lap lap = lapOptional.get();

                // Filtra os parâmetros com a Key fornecida
                List<Parameter> matchingParameters = new ArrayList<>();
                List<ParameterResponse> response = matchingParameters.stream()
                        .map(ParameterResponse::new)
                        .collect(Collectors.toList());

                if (!matchingParameters.isEmpty()) {
                    System.out.println(response.toString());
                    return ResponseEntity.ok(response);
                }
                else return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Parameter with key [" + key + "] not found in this lap.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lap not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

        /**
         * Endpoint para buscar todos os parâmetros de uma volta específica (Lap).
         *
         * Este endpoint recebe o nome da volta (`lap-name`) e retorna a lista de parâmetros associados à volta
         *
         *[
         *   {
         *     "key": "speed",
         *     "value": "75",
         *     "added": "2023-09-21T15:30:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
         *   },
         *   {
         *     "key": "temperature",
         *     "value": "78",
         *     "added": "2023-09-21T15:35:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
         *   },
         *   {
         *     "key": "rpm",
         *     "value": "6677",
         *     "added": "2023-09-21T15:40:25.123456Z" <--- Converta isso no frontend para algo que faça mais sentido
         *   }
         * ]
         *
         * Exemplo de URL:
         * GET /laps/Volta%201/parameters <--- Formatação de URI no lap-name
         *
         * Respostas possíveis:
         * - 200 OK: Se a volta for encontrada.
         *   Corpo da resposta: JSON com a lista de parâmetros.
         *
         * - 404 Not Found: Se a volta não for encontrada.
         *   Corpo da resposta: "Lap not found".
         *
         * - 500 Internal Server Error: Se ocorrer algum erro durante o processamento da solicitação.
         *
         * @return ResponseEntity contendo a lista de parâmetros ou uma mensagem de erro.
         */
// Em MainController.java

    /**
     * Endpoint para buscar todos os parâmetros de uma volta específica (Lap).
     * Exemplo de URL: GET /main/Volta%201/parameters
     */
    @GetMapping("/{lapName}/parameters")
    public ResponseEntity<?> getLapParameters(@PathVariable String lapName) {
        Optional<Lap> lapOptional = lapRepository.findByName(lapName);

        if (lapOptional.isPresent()) {
            Lap lap = lapOptional.get();
            List<Parameter> parameters = lap.getParameters();

            if (parameters.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No parameters found for this lap.");
            }
            // Idealmente, aqui também retornaria um DTO
            return ResponseEntity.ok(parameters);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lap not found.");
        }
    }

}
