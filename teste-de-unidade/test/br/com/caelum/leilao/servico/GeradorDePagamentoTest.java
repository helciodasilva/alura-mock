package br.com.caelum.leilao.servico;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamentoTest {

	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {

		RepositorioDeLeiloes leiloes = Mockito.mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = Mockito.mock(RepositorioDePagamentos.class);

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		Mockito.when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		// aqui passamos uma instância concreta de Avaliador
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador());
		gerador.gera();

		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		Mockito.verify(pagamentos).salva(argumento.capture());
		Pagamento pagamentoGerado = argumento.getValue();
		Assert.assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
	}
}
