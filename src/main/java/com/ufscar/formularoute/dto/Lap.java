package com.ufscar.formularoute.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
A entidade `Lap` representa uma volta completa em um sistema de telemetria.
Esse objeto armazena todos os dados de telemetria capturados durante a volta, como leituras de sensores, parâmetros de desempenho, e outros dados relevantes associados ao período da volta.
Quando um usuário deseja analisar uma volta específica (seja por nome ou data), o sistema pode buscar e retornar uma instância de `Lap` contendo todos os parâmetros relevantes para aquela volta.

A estrutura de `Lap` é projetada para ser flexível e eficiente, permitindo a coleta, armazenamento e consulta de grandes volumes de dados de telemetria ao longo do tempo. Cada `Lap` pode conter diversos parâmetros, como temperatura, velocidade, aceleração, entre outros, todos organizados e vinculados à volta específica em que foram coletados.

### Principais campos da entidade `Lap`:

- `id`: Identificador único da volta. Gerado automaticamente utilizando um UUID para garantir a unicidade e permitir operações de banco de dados mais eficientes.

- `name`: Um nome descritivo para a volta, que pode ser utilizado para fins de identificação. Pode ser o nome da corrida ou qualquer outro identificador específico que o sistema utilize.

- `added`: Um timestamp que indica quando a volta foi registrada no sistema, usando o fuso horário de "America/Sao_Paulo" com precisão de milissegundos. Isso permite organizar e analisar os dados de telemetria no contexto temporal correto.

- `parameters`: Uma lista de objetos `Parameter`, que representam os diferentes parâmetros de telemetria associados a essa volta. Cada parâmetro contém informações sobre uma métrica específica, como velocidade ou temperatura, e está vinculado ao objeto `Lap`. Esse relacionamento OneToMany permite que uma volta armazene múltiplos parâmetros sem limitações, tornando a estrutura escalável para armazenar qualquer número de dados coletados durante a volta.

### Relacionamentos:

- A entidade `Lap` tem um relacionamento OneToMany com a entidade `Parameter`. Isso significa que uma única volta pode ter vários parâmetros associados. Os parâmetros são mapeados por meio do campo `asset` na entidade `Parameter`.

- O `cascade = CascadeType.ALL` garante que qualquer operação realizada em `Lap` (como salvar, atualizar ou deletar) será refletida automaticamente nos parâmetros associados. Isso facilita a manutenção da integridade dos dados, garantindo que os parâmetros relacionados sejam removidos ou atualizados conforme necessário.

- `orphanRemoval = true` assegura que, ao remover um parâmetro de uma volta, o dado órfão seja excluído do banco de dados, prevenindo a criação de dados residuais desnecessários.

### Integração com o Frontend:

No lado do frontend, quando um usuário deseja visualizar ou analisar uma volta específica, o sistema pode buscar uma instância de `Lap` com base no nome ou data da volta. Os parâmetros associados são então retornados como parte da resposta, permitindo ao sistema agrupar e visualizar as leituras de telemetria em gráficos ou tabelas. Isso oferece ao usuário uma visão completa e detalhada do desempenho da volta em questão.

Essa estrutura robusta permite uma análise detalhada das voltas de forma eficiente e organizada, facilitando a manipulação de grandes volumes de dados e permitindo análises avançadas de desempenho.
*/

@Entity
@Table(name="lap")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@NamedEntityGraph(
        name = "Lap.withParameters",
        attributeNodes = @NamedAttributeNode("parameters")
)
public class Lap {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id")
    @JsonIgnore
    private UUID id;

    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "America/Sao_Paulo")
    @Column(name = "added")
    private ZonedDateTime added = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));

    @OneToMany(targetEntity = Parameter.class, mappedBy = "lap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Parameter> parameters = new ArrayList<>();
}
