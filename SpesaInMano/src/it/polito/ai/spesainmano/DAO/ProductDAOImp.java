package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Category;
import it.polito.ai.spesainmano.model.Price;
import it.polito.ai.spesainmano.model.Product;
import it.polito.ai.spesainmano.model.ProductType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.polito.ai.spesainmano.db.*;

public class ProductDAOImp implements ProductDAO{
	Connection con;
	
	@Override
	public Product insert(Product p) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "insert into product(id_product_type, name, barcode, brand, quantity, measure_unit) values(?, ?, ?, ?, ?, ?)";
		try {
			ps = con.prepareStatement(query,  Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, p.getId_product_type().getId_product_type());
			ps.setString(2, p.getName());
			ps.setString(3, p.getBarcode());
			ps.setString(4, p.getBrand());
			ps.setString(5, p.getQuantity());
			ps.setString(6, p.getMeasure_unit());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if(rs.next()){
	            // Update the id in the returned object. This is important as this value must be returned to the client.
	            int id = rs.getInt(1);
	            p.setId_product(id);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return p;
	}

	@Override
	public Product getProductByBarcode(String barcode) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		Product p = null;
		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit, c.id_category, c.name, pt.id_product_type, pt.name, pt.presentation from product p, product_type pt, category c where pt.id_category=c.id_category and p.id_product_type=pt.id_product_type and p.barcode = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setString(1, barcode);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
	          	p = new Product();
				p.setId_product(rs.getInt(1));
	            p.setName(rs.getString(2));
	            p.setBrand(rs.getString(3));
	            p.setQuantity(rs.getString(4));
	            p.setMeasure_unit(rs.getString(5));
	            Category c = new Category();
	            c.setId_category(rs.getInt(6));
	            c.setName(rs.getString(7));
	            ProductType pt = new ProductType();
	            pt.setId_product_type(rs.getInt(8));
	            pt.setName(rs.getString(9));
	            pt.setPresentation(rs.getString(10));
	            pt.setId_category(c);
	            p.setId_product_type(pt);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return p;
	}

	@Override
	public List<Product> getProductsByProductType(int productTypeId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		List<Product> products = new ArrayList<Product>();
		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit, c.id_category, c.name, pt.id_product_type, pt.name, pt.presentation from product p, product_type pt, category c where pt.id_category=c.id_category and p.id_product_type=pt.id_product_type and p.id_product_type = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, productTypeId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
	          	Product p = new Product();
				p.setId_product(rs.getInt(1));
	            p.setName(rs.getString(2));
	            p.setBrand(rs.getString(3));
	            p.setQuantity(rs.getString(4));
	            p.setMeasure_unit(rs.getString(5));
	            Category c = new Category();
	            c.setId_category(rs.getInt(6));
	            c.setName(rs.getString(7));
	            ProductType pt = new ProductType();
	            pt.setId_product_type(rs.getInt(8));
	            pt.setName(rs.getString(9));
	            pt.setPresentation(rs.getString(10));
	            pt.setId_category(c);
	            p.setId_product_type(pt);
	            products.add(p);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return products;
	}

	@Override
	public Product getProduct(int productId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		Product p = null;
		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit, c.id_category, c.name, pt.id_product_type, pt.name, pt.presentation from product p, product_type pt, category c where pt.id_category=c.id_category and p.id_product_type=pt.id_product_type and p.id_product = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, productId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
	          	p = new Product();
				p.setId_product(rs.getInt(1));
	            p.setName(rs.getString(2));
	            p.setBrand(rs.getString(3));
	            p.setQuantity(rs.getString(4));
	            p.setMeasure_unit(rs.getString(5));
	            Category c = new Category();
	            c.setId_category(rs.getInt(6));
	            c.setName(rs.getString(7));
	            ProductType pt = new ProductType();
	            pt.setId_product_type(rs.getInt(8));
	            pt.setName(rs.getString(9));
	            pt.setPresentation(rs.getString(10));
	            pt.setId_category(c);
	            p.setId_product_type(pt);
			}
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return p;
	}

	@Override
	public List<Price> getSimilarProductPrices(Product product, int supermarketId) throws SQLException {
		con = ConnectionPoolManager.getPoolManagerInstance().getConnectionFromPool();
		PreparedStatement ps = null;
		String query = "select pri.price, pro.name, pro.brand, pro.quantity, pro.measure_unit  "
					+ "from price pri, product pro "
					+ "where pri.id_product = pro.id_product and pro.id_product_type = ? "
					+ "and pro.id_product != ? and pro.quantity = ? and pro.measure_unit = ? and pri.id_supermarket = ? " 
					+ "and pri.id_price in(select max(id_price) " 
						+ "from price "
						+ "where id_supermarket = ? "
						+ "group by id_product) "
					+ "order by pri.price";
		List<Price> prices = new ArrayList<Price>();
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, product.getId_product_type().getId_product_type());
			ps.setInt(2, product.getId_product());
			ps.setString(3, product.getQuantity());
			ps.setString(4, product.getMeasure_unit());
			ps.setInt(5, supermarketId);
			ps.setInt(6, supermarketId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
	          Price p = new Price();
	          p.setPrice(rs.getFloat(1));
	          Product pro = new Product();
	          pro.setName(rs.getString(2));
	          pro.setBrand(rs.getString(3));
	          pro.setQuantity(rs.getString(4));
	          pro.setMeasure_unit(rs.getString(5));
	          p.setId_product(pro);
	          prices.add(p);
			}
			
		}catch (SQLException e) {
			 throw e;
		} finally{
			ConnectionPoolManager.getPoolManagerInstance().returnConnectionToPool(con);
		}
		return prices;
	}

}
