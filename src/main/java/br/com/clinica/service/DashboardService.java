package br.com.clinica.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.domain.enums.StatusConsulta;
import br.com.clinica.domain.model.Consulta;
import br.com.clinica.dto.response.ConsultaHojeResponse;
import br.com.clinica.dto.response.ConsultasPorEspecialidadeResponse;
import br.com.clinica.dto.response.ConsultasPorMesResponse;
import br.com.clinica.dto.response.ConsultasPorStatusResponse;
import br.com.clinica.dto.response.DashboardResumoResponse;
import br.com.clinica.repository.ConsultaRepository;
import br.com.clinica.repository.MedicoRepository;
import br.com.clinica.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    // --- RESUMO (cards) ----------------------------------------------------

    /** Explicação código para definir o início e fim do dia:
     * LocalDate.now() -> data atual (ex: 2026‑04‑17).
     * .atStartOfDay() converte isso para um LocalDateTime com hora 00:00:00 (meio‑noite do início do dia).
     * Resultado: 2026‑04‑17T00:00:00.000 -> é o início do dia utlizado como inicioDia / inicioMes.
     * 
     * .plusDays(1): Adiciona 1 dia ao inicioDia (começo do dia). Ex.: de 2026‑04‑17T00:00 -> 2026‑04‑18T00:00.
     * 
     * .minusNanos(1): ubtrai 1 nanossegundo dessa data. Resultado: 2026‑04‑17T23:59:59.999999999
     * Praticamente o último instante do dia.
     * 
     * Ou seja, juntos:
     * inicioDia -> 00:00:00.000 do dia atual.
     * fimDia -> 23:59:59.999999999 do mesmo dia.
     * 
     * Vale ressaltar que esse padrão é comum para definir um intervalo “de hoje” em filtros no banco:
     * BETWEEN inicioDia AND fimDia
     * Garante que todas as consultas do dia inteiro estejam incluídas, sem perder registros no limite de hora.
     * 
     * Por que NÃO usei 23:59:59 no SQL?
     * Ao usar minusNanos(1) em vez de 23:59:59 permite lidar com timestamps com precisão até nanossegundos. 
     * Em alguns bancos, se o campo tem milissegundos, minusNanos(1) evita “furar” o último milissegundo por 
     * causa de arredondamento ou aritmética de ponto flutuante.
     * 
     * Em resumo prático:
     * .atStartOfDay() -> “meia‑noite do dia”.
     * plusDays(1).minusNanos(1) -> “último nanossegundo antes da próxima meia‑noite”.
     * Essa é uma forma limpa de fazer que diz:
     * Quero todo o dia inteiro, desde o primeiro instante até o último instante antes do próximo dia.
     */

    /**
     * Retorna resumo geral do dashboard (cards principais).
     * 
     * <p>Métricas calculadas:</p>
     * <ul>
     * <li>Total pacientes e médicos ativos</li>
     * <li>Consultas realizadas hoje e no mês corrente</li>
     * <li>Total de consultas AGENDADAS</li>
     * </ul>
     * 
     * @return Response com todas as métricas principais do dashboard
     */
    @Transactional(readOnly = true)  // roda dentro de uma transação de leitura - leitura: não faz insert/update/delete, só consulta. 
    public DashboardResumoResponse getResumo() {

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1).minusNanos(1);

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = inicioMes.plusMonths(1).minusNanos(1);

        long totalPacientes = pacienteRepository.countByAtivoTrue();
        long totalMedicos = medicoRepository.countByAtivoTrue();

        long consultasHoje = consultaRepository.findConsultasDoDia(inicioDia, fimDia).size();
        long consultasMes = consultaRepository.findByDataHoraBetween(inicioMes, fimMes).size();
        
        long agendadas = consultaRepository.countByStatus(StatusConsulta.AGENDADA);

        long realizadasMes = consultaRepository.findByDataHoraBetween(inicioMes, fimMes)
                .stream()
                .filter(c -> c.getStatus() == StatusConsulta.REALIZADA)
                .count();
        
        return new DashboardResumoResponse(
            totalPacientes,
            totalMedicos,
            consultasHoje,
            consultasMes,
            agendadas,
            realizadasMes
        );
    }

    // --- CONSULTAS DO DIA ----------------------------------------------------

    /**
     * Lista todas as consultas agendadas para hoje.
     * 
     * <p>Período: 00:00 até 23:59 do dia atual, apenas status AGENDADA.</p>
     * <p>Query personalizada para dashboard de próximas consultas:
     * <code> SELECT c FROM Consulta c WHERE c.dataHora BETWEEN :inicio AND :fim AND c.status = 'AGENDADA' ORDER BY c.dataHora ASC </code></p>
     * 
     * @return Lista ordenada das consultas de hoje
     */
    @Transactional(readOnly = true)
    public List<ConsultaHojeResponse> getConsultasHoje() {

        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = inicio.plusDays(1).minusNanos(1);

        return consultaRepository.findConsultasDoDia(inicio, fim)
                .stream()
                .map(this::toConsultaHojeResponse)
                .toList();
    }

    // --- CONSULTAS POR STATUS ----------------------------------------------------
    
    /**
     * Retorna contagem de consultas agrupadas por status.
     * 
     * <p>Todos os 5 status possíveis: AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, NÃO COMPARECEU.</p>
     * 
     * @return Response com total por cada status
     */
    @Transactional(readOnly = true)
    public ConsultasPorStatusResponse getConsultasPorStatus() {

        return new ConsultasPorStatusResponse(
            consultaRepository.countByStatus(StatusConsulta.AGENDADA),
            consultaRepository.countByStatus(StatusConsulta.CONFIRMADA),
            consultaRepository.countByStatus(StatusConsulta.REALIZADA),
            consultaRepository.countByStatus(StatusConsulta.CANCELADA),
            consultaRepository.countByStatus(StatusConsulta.NAO_COMPARECEU)
        );
    }

    // --- CONSULTAS POR MÊS (últimos 6 meses) ----------------------------------------------------

    /**
     * Por exemplo:
     * LocalDate.now() -> hoje (ex: 2026‑04‑17).
     * .minusMonths(5) -> volta 5 meses (ex: 2025‑11‑17).
     * .withDayOfMonth(1) -> primeiro dia desse mês (2025‑11‑01).
     * .atStartOfDay() -> transforma em LocalDateTime às 00:00 (2025‑11‑01T00:00:00).
     * Ou seja, dataInicio é o início do 6º mês antes do mês atual.
     * Isso fará o banco trazer resultados de 6 meses completos (ex: de novembro/2025 até abril/2026).
     * 
     * A variável "resultado" é uma lista de arrays -> cada Object[] representa uma “linha” do resultado.
     * Cada linha terá, por exemplo:
     * índice 0: número do mês (1–12)
     * índice 1: ano (int)
     * índice 2: total de consultas desse mês (bigint/long)
     * O objetivo é fazer um COUNT agrupado por mês/ano no banco.
     * 
     * Transformação com stream().map(...)
     * Ocorre a extração dos campos, row[0], row[1], row[2] vêm do Object[] retorno da query.
     * ((Number) ...).intValue() converte Integer/Long do banco para primitivos int/long.
     * 
     * A variável "nomeMes" gera o nome do mês em português.
     * LocalDate.of(ano, numeroMes, 1) cria uma data válida dia 1 naquele mês/ano.
     * .getMonth() -> enum Month (JAN, FEB, ..., DEC).
     * .getDisplayName(TextStyle.SHORT, ...) -> retorna o nome abreviado do mês em português - por exemplo:
     * 1 -> "jan"
     * 4 -> "abr"
     * etc.
     * 
     * E retorna a criação do DTO response com o texto concatenado com o ano.
    */

    /**
     * Retorna evolução de consultas dos últimos 6 meses.
     * 
     * <p> O resultado é uma lista de objetos com o formato: {“mesAno”: “abr/2026”, “ano”: 2026, “mes”: 4, “total”: 120} </p>
     * 
     * <p>Query analítica agrupa por mês/ano com nomes em português (Jan/2026, Fev/2026, etc.).</p>
     * 
     * <p>Query personalizada para dashboard de próximas consultas:
     * <code> SELECT MONTH(c.dataHora) as mes, YEAR(c.dataHora) as ano, COUNT(c) as total FROM Consulta c WHERE c.dataHora >= :dataInicio GROUP BY YEAR(c.dataHora), MONTH(c.dataHora) ORDER BY YEAR(c.dataHora), MONTH(c.dataHora)</code></p>
     * 
     * @return Lista ordenada cronologicamente [mês/ano, total]
     */
    @Transactional(readOnly = true)
    public List<ConsultasPorMesResponse> getConsultasPorMes() {

        LocalDateTime dataInicio = LocalDate.now().minusMonths(5).withDayOfMonth(1).atStartOfDay();

        List<Object[]> resultado = consultaRepository.countByMes(dataInicio);

        return resultado.stream().map(
            row -> {
                int numeroMes = ((Number) row[0]).intValue();
                int ano       = ((Number) row[1]).intValue();
                long total    = ((Number) row[2]).longValue();

                // Converte número do mês para nome abreviado em português
                String nomeMes = LocalDate
                        .of(ano, numeroMes, 1)
                        .getMonth()
                        .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));

                // retorno - exemplo:  {“mesAno”: “abr/2026”, “ano”: 2026, “mes”: 4, “total”: 120}
                return new ConsultasPorMesResponse(
                        nomeMes + "/" + ano,
                        ano,
                        numeroMes,
                        total
                );

            }).toList();
    }

    // --- CONSULTAS POR ESPECIALIDADE ----------------------------------------------------

    /**
     * Esse método monta um resumo das consultas agrupadas por especialidade médica, já com percentuais em relação ao total de consultas.
     * 
     * (1) Vai buscar no banco o total de consultas agrupadas por especialidade. O resultado é uma lista de Object[], onde cada Object[] tem:
     * - indice 0: nome da especialidade (String)
     * - indice 1: quantidade total de consultas para essa especialidade (Number, que será convertido para long).
     * 
     * (2) Calculo do total geral de consultas somando os totais de cada especialidade. 
     * Transforma cada linha em um long (valor total de consultas dessa especialidade) e soma tudo.
     * O totalGeral é o total de consultas de todas as especialidades juntas, usado depois para calcular o percentual.
     * 
     * (3) Mapeamento para o DTO com percentual:
     *   Ocorre a extração dos campos: 
     *     1. especialidade vem de row[0] (nome da especialidade).
     *     2. total vem de row[1] (número de consultas dessa especialidade).
     *   Ocorre o cálculo do percentual:
     *     Se o total > 0, o percentual é -> percentual = (total * 100.0 / totalGeral)
     *     Depois é multiplicado por 10, arredondando com Math.round e dividido por 10 de novo para arredondar para 1 casa decimal.
     *     Exemplo: total = 25, totalGeral = 100 -> percentual = (25 * 100.0 / 100) = 25.0 -> arredondado para 25.0.
     *     Se o totalGeral for 0 (para evitar divisão por zero), o percentual é definido como 0.0.
     * 
     *  (4) Criação do DTO ConsultasPorEspecialidadeResponse para cada linha, cria um objeto por especialidade com:
     *   - especialidade (nome)
     *   - total (número de consultas)
     *   - percentual (proporção em % em relação ao total de consultas).
    */

    /**
     * Retorna ranking de consultas por especialidade com percentuais.
     * 
     * <p>Calcula percentual de cada especialidade sobre o total geral, ordenado por volume DESC.</p>
     * 
     * <p>Query personalizada para dashboard de próximas consultas:
     * <code> SELECT e.nome as especialidade, COUNT(c) as total FROM Consulta c JOIN c.medico m JOIN m.especialidades e GROUP BY e.nome ORDER BY COUNT(c) DESC </code></p>
     * 
     * @return Lista de especialidades [nome, total, percentual%]
     */
    @Transactional(readOnly = true)
    public List<ConsultasPorEspecialidadeResponse> getConsultasPorEspecialidade() {

        // Lista de Object[], onde cada array representa uma especialidade e seu total de consultas. Exemplo de linha: ["Cardiologia", 50]
        List<Object[]> resultado = consultaRepository.countByEspecialidade();

        // Calcula o total para percentual
        long totalGeral = resultado.stream()
            .mapToLong(row -> ((Number) row[1]).longValue())
            .sum();
        
        return resultado.stream().map(row -> {
            String especialidade = (String) row[0];
            long total = ((Number) row[1]).longValue();
            double percentual = totalGeral > 0 ? Math.round((total * 100.0 / totalGeral) * 10.0) / 10.0 : 0.0;
            
            /**
             * Exemplo de retorno:
             * { "especialidade": "Cardiologia", "total": 50, "percentual": 25.0 }
            */
            return new ConsultasPorEspecialidadeResponse(
                especialidade, total, percentual);
        }).toList();
    }


    // --- PRÓXIMAS CONSULTAS (7 dias) ----------------------------------------------------

    /**
     * Busca as próximas consultas agendadas para os próximos 7 dias a partir de agora.
     * 
     * <p>Filtra apenas AGENDADA/CONFIRMADA, ordenadas por horário.</p>
     * 
     * <p>Query personalizada para dashboard de próximas consultas:
     * <code>SELECT c FROM Consulta c WHERE c.dataHora BETWEEN :agora AND :limite AND c.status IN ('AGENDADA', 'CONFIRMADA') ORDER BY c.dataHora ASC</code></p>
     * 
     * <p>Utilizado para mostrar as próximas consultas no dashboard, com detalhes do paciente e médico.</p>
     * 
     * @return Lista de consultas agendadas para os próximos 7 dias
     */
    @Transactional(readOnly = true)
    public List<ConsultaHojeResponse> getProximasConsultas() {

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite = agora.plusDays(7);

        return consultaRepository.findProximasConsultas(agora, limite).stream()
                .map(this::toConsultaHojeResponse)
                .toList();
    }


    // --- MÉTODO AUXILIAR ----------------------------------------------------

    /**
     * Método auxiliar para converter uma entidade Consulta em um DTO ConsultaHojeResponse, que é usado para exibir as consultas do dia e 
     * próximas consultas no dashboard.
     * 
     * <p>Método auxiliar privado que extrai primeira especialidade do médico.</p>
     * 
     * @param c Consulta a ser convertida 
     * @return ConsultaHojeResponse com os dados formatados para exibição no dashboard
     */
    private ConsultaHojeResponse toConsultaHojeResponse(Consulta c) { 
        
        String especialidade = c.getMedico().getEspecialidades().isEmpty() ? "Não informado" : c.getMedico().getEspecialidades().get(0).getNome();
        
        return new ConsultaHojeResponse(c.getId(),
            c.getDataHora(),
            c.getPaciente().getNome(),
            c.getMedico().getNome(),
            especialidade,
            c.getStatus()
        );
    }

}
