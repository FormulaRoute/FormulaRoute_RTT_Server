package com.ufscar.formularoute.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
Este objeto `Parameter` foi projetado para armazenar e representar os valores de parâmetros coletados de sistemas de telemetria em tempo real.
Ele utiliza a JPA para se relacionar com a entidade "Volta" através de um relacionamento ManyToOne.
Isso significa que múltiplos parâmetros (que representam diferentes leituras de sensores, como velocidade, temperatura, etc.) são coletados durante uma volta, e cada conjunto de parâmetros é associado a uma entidade `Volta`.
Posteriormente, esses parâmetros são processados e exibidos em gráficos conforme as suas chaves (`key`) e valores (`value`), e são organizados pelo campo de tempo (`added`).

A principal vantagem de utilizar uma entidade genérica como `Parameter` em vez de criar campos específicos para cada parâmetro no banco de dados é a flexibilidade para adicionar novos tipos de parâmetros no futuro sem a necessidade de modificar a estrutura do banco de dados ou alterar os Data Transfer Objects (DTOs) da aplicação.
Isso torna o sistema mais adaptável e menos suscetível a mudanças no código base quando novos parâmetros de telemetria precisam ser incorporados.

Os campos principais da entidade `Parameter` são:
- `key`: O nome ou identificador do parâmetro, como "velocidade", "temperatura", "pressão", etc.
- `value`: O valor do parâmetro coletado, que pode ser uma string que representa um número, uma leitura de sensor ou outro tipo de dado.
- `added`: A data e a hora exatas em que o valor foi coletado, armazenado com precisão de milissegundos, utilizando o fuso horário da América/São Paulo.

Na interface com o frontend, o sistema terá a responsabilidade de:
1. Recolher todos os parâmetros do banco de dados ou de uma API.
2. Agrupar os parâmetros em listas com base em seu campo `key` (ou seja, criar uma lista para todos os parâmetros de "velocidade", outra para "temperatura", etc.).
3. Ordenar essas listas com base no campo `added`, garantindo que os dados sejam apresentados na ordem em que foram coletados.
4. Exibir esses dados em gráficos adequados, mostrando a evolução dos parâmetros ao longo do tempo.

Isso permite que o sistema exiba de forma dinâmica e eficiente os dados de telemetria, independentemente de quantos ou quais parâmetros estão sendo monitorados.
*/

@Entity
@Table(name="parameter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "id")
    @JsonIgnore
    private UUID id;

    private String value;
    private String key;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "America/Sao_Paulo")
    @Column(name = "added")
    private ZonedDateTime added = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "lap_id", nullable = false)
    @JsonIgnore
    private Lap lap;
}
