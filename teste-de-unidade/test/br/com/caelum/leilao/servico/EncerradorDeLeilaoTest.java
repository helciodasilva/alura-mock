package br.com.caelum.leilao.servico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		RepositorioDeLeiloes daoFalso = Mockito.mock(LeilaoDao.class);
		Mockito.when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		Assert.assertEquals(2, encerrador.getTotalEncerrados());
		Assert.assertTrue(leilao1.isEncerrado());
		Assert.assertTrue(leilao2.isEncerrado());
	}

	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {

		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(ontem).constroi();

		RepositorioDeLeiloes daoFalso = Mockito.mock(RepositorioDeLeiloes.class);
		Mockito.when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		Assert.assertEquals(0, encerrador.getTotalEncerrados());
		Assert.assertFalse(leilao1.isEncerrado());
		Assert.assertFalse(leilao2.isEncerrado());
	}
	
	@Test
	public void naoDeveEncerrarLeiloesCasoNaoHajaNenhum() {

		RepositorioDeLeiloes daoFalso = Mockito.mock(RepositorioDeLeiloes.class);
		Mockito.when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		Assert.assertEquals(0, encerrador.getTotalEncerrados());
	}

	@Test
	public void deveAtualizarLeiloesEncerrados() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();

		RepositorioDeLeiloes daoFalso = Mockito.mock(RepositorioDeLeiloes.class);
		Mockito.when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso);
		encerrador.encerra();

		// verificando que o metodo atualiza foi realmente invocado!
		Mockito.verify(daoFalso).atualiza(leilao1);
	}

}
