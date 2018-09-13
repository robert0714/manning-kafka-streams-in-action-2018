package bbejeck.util.datagen;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import bbejeck.clients.producer.MockDataProducer;
import bbejeck.model.BeerPurchase;
import bbejeck.model.ClickEvent;
import bbejeck.model.PublicTradedCompany;
import bbejeck.model.Purchase;
import bbejeck.model.StockTransaction;
import bbejeck.util.datagen.DataGenerator.Customer;

public class DataGeneratorTest {

	@Test
	public void testSetTimestampGenerator() throws Exception {

		// 日期時間產生器(每次呼叫都增加750 Milliseconds )
		final CustomDateGenerator dateGenerator = CustomDateGenerator
				.withTimestampsIncreasingBy(Duration.ofMillis(750));

		// lambda
		DataGenerator.setTimestampGenerator(dateGenerator::get);

		final Date customDateGeneratorDate = dateGenerator.get();

		// old method before jdk8
		Supplier<Date> generator = new Supplier<Date>() {
			@Override
			public Date get() {
				return customDateGeneratorDate;
			}
		};

		DataGenerator.setTimestampGenerator(generator);

		System.out.println("customDateGeneratorDate: " + customDateGeneratorDate);

		// 產生購物假資料進行驗證。
		Purchase purchase = DataGenerator.generatePurchase();
		System.out.println(ToStringBuilder.reflectionToString(purchase, ToStringStyle.JSON_STYLE));
		final Date purchaseDate = purchase.getPurchaseDate();
		
		System.out.println("purchaseDate: " + purchaseDate);

		Date calculateDate = DateUtils.addMilliseconds(purchaseDate, 750*2);
		// 驗證假日期有無一致
		Assert.assertEquals(customDateGeneratorDate.getTime(), calculateDate.getTime());
	}

	@Test
	public void testGenerateRandomText() throws Exception {
		List<String> textArea = DataGenerator.generateRandomText();
		if (textArea != null && textArea.size() > 0) {
			for (String line : textArea) {
				System.out.println(line);
			}
		}
	}

	@Test
	public void testGeneratePublicTradedCompanies() throws Exception {
		final List<PublicTradedCompany> companies = DataGenerator.generatePublicTradedCompanies(100);
		if (companies != null && companies.size() > 0) {
			for (PublicTradedCompany commpany : companies) {
				System.out.println(ToStringBuilder.reflectionToString(commpany, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGeneratePublicTradedCompaniesForInteractiveQueries() throws Exception {
		final List<PublicTradedCompany> companies = DataGenerator.generatePublicTradedCompaniesForInteractiveQueries();
		if (companies != null && companies.size() > 0) {
			for (PublicTradedCompany commpany : companies) {
				System.out.println(ToStringBuilder.reflectionToString(commpany, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGenerateFinancialNews() throws Exception {
		
		List<String> textArea = DataGenerator.generateFinancialNews();
		if (textArea != null && textArea.size() > 0) {
			for (String line : textArea) {
				System.out.println(line);
			}
		}
	}

	@Test
	public void testGenerateDayTradingClickEvents() throws Exception {
		final List<PublicTradedCompany> companies = DataGenerator.generatePublicTradedCompanies(100);
		final List<ClickEvent> events = DataGenerator.generateDayTradingClickEvents(2, companies);
		if (events != null && events.size() > 0) {
			for (ClickEvent event : events) {
				System.out.println(ToStringBuilder.reflectionToString(event, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGeneratePurchase() throws Exception {
		Purchase purchase = DataGenerator.generatePurchase();
		System.out.println(ToStringBuilder.reflectionToString(purchase, ToStringStyle.JSON_STYLE));
	}

	@Test
	public void testGeneratePurchases() throws Exception {
		List<Purchase> purchases = DataGenerator.generatePurchases(1, 1);
		if (purchases != null && purchases.size() > 0) {
			for (Purchase purchase : purchases) {
				System.out.println(ToStringBuilder.reflectionToString(purchase, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGenerateBeerPurchases() throws Exception {
		List<BeerPurchase> beerPurchases = DataGenerator.generateBeerPurchases(10);
		if (beerPurchases != null && beerPurchases.size() > 0) {
			for (BeerPurchase purchase : beerPurchases) {
				System.out.println(ToStringBuilder.reflectionToString(purchase, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGenerateStockTransaction() throws Exception {
		StockTransaction tx = DataGenerator.generateStockTransaction();
		System.out.println(ToStringBuilder.reflectionToString(tx, ToStringStyle.JSON_STYLE));
	}

	@Test
	public void testGenerateStockTransactionsForIQ() throws Exception {
		List<StockTransaction> txs = DataGenerator.generateStockTransactionsForIQ(10);
		if (txs != null && txs.size() > 0) {
			for (StockTransaction tx : txs) {
				System.out.println(ToStringBuilder.reflectionToString(tx, ToStringStyle.JSON_STYLE));
			}
		}
	}
	
	@Test
	public void testGenerateStockTransactions() throws Exception {
		final List<PublicTradedCompany> companies = DataGenerator.generatePublicTradedCompanies(100);
		List<Customer> customers = DataGenerator.generateCustomers(10);
		List<StockTransaction> txs = DataGenerator.generateStockTransactions(customers, companies, 15);
		if (txs != null && txs.size() > 0) {
			for (StockTransaction tx : txs) {
				System.out.println(ToStringBuilder.reflectionToString(tx, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGenerateCustomers() throws Exception {
		List<Customer> customers = DataGenerator.generateCustomers(10);
		if (customers != null && customers.size() > 0) {
			for (Customer tx : customers) {
				System.out.println(ToStringBuilder.reflectionToString(tx, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testStockTicker() throws Exception {
		List<PublicTradedCompany> companies = DataGenerator.stockTicker(2);
		if (companies != null && companies.size() > 0) {
			for (PublicTradedCompany commpany : companies) {
				System.out.println(ToStringBuilder.reflectionToString(commpany, ToStringStyle.JSON_STYLE));
			}
		}
	}

	@Test
	public void testGenerateCustomersForInteractiveQueries() throws Exception {
		List<Customer> customers = DataGenerator.generateCustomersForInteractiveQueries();
		if (customers != null && customers.size() > 0) {
			for (Customer tx : customers) {
				System.out.println(ToStringBuilder.reflectionToString(tx, ToStringStyle.JSON_STYLE));
			}
		}
	}

	

}
