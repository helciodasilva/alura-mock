package br.com.caelum.leilao.servico;

import java.util.Arrays;
import java.util.Calendar;

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
import br.com.caelum.leilao.infra.relogio.Relogio;

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
	
	@Test
	public void deveEmpurrarParaOProximoDiaUtil() {
		RepositorioDeLeiloes leiloes = Mockito.mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = Mockito.mock(RepositorioDePagamentos.class);
		Relogio relogio = Mockito.mock(Relogio.class);

		// dia 7/abril/2012 eh um sabado
		Calendar sabado = Calendar.getInstance();
		sabado.set(2012, Calendar.APRIL, 7);

		// ensinamos o mock a dizer que "hoje" é sabado!
		Mockito.when(relogio.hoje()).thenReturn(sabado);

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		Mockito.when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
		gerador.gera();

		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		Mockito.verify(pagamentos).salva(argumento.capture());
		Pagamento pagamentoGerado = argumento.getValue();

		Assert.assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		Assert.assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}
	
	@Test
	public void deveEmpurrarPagamentoNoDomingoParaOProximoDiaUtil() {
		RepositorioDeLeiloes leiloes = Mockito.mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = Mockito.mock(RepositorioDePagamentos.class);
		Relogio relogio = Mockito.mock(Relogio.class);

		Calendar domingo = Calendar.getInstance();
		domingo.set(2012, Calendar.APRIL, 8);
		Mockito.when(relogio.hoje()).thenReturn(domingo);

		Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("José da Silva"), 2000.0)
				.lance(new Usuario("Maria Pereira"), 2500.0).constroi();

		Mockito.when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamentos, new Avaliador(), relogio);
		gerador.gera();

		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		Mockito.verify(pagamentos).salva(argumento.capture());
		Pagamento pagamentoGerado = argumento.getValue();

		Assert.assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		Assert.assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}
	
}
