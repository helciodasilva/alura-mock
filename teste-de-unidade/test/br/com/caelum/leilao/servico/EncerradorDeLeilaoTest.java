package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {

		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();

		LeilaoDao dao = new LeilaoDao();
		dao.salva(leilao1);
		dao.salva(leilao2);
		
		List<Leilao> encerrados = dao.encerrados();
		
		// mas como passo os leiloes criados para o EncerradorDeLeilao,
		// já que ele os busca no DAO?

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao();
		encerrador.encerra();

		Assert.assertEquals(2, encerrados.size());
		assertTrue(encerrados.get(0).isEncerrado());
		assertTrue(encerrados.get(1).isEncerrado());
	}

}
