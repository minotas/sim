package it.polito.ai.spesainmano.DAO;

import it.polito.ai.spesainmano.model.Category;
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
		String query = "insert into product(id_product_type, name, barcode, brand, quantity, measure_unit, image) values(?, ?, ?, ?, ?, ?, ?)";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, p.getId_product_type().getId_product_type());
			ps.setString(2, p.getName());
			ps.setString(3, p.getBarcode());
			ps.setString(4, p.getBrand());
			ps.setString(5, p.getQuantity());
			ps.setString(6, p.getMeasure_unit());
			ps.setString(7, p.getImage());
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
		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit, p.image, c.id_category, c.name, pt.id_product_type, pt.name, pt.presentation from product p, product_type pt, category c where pt.id_category=c.id_category and p.id_product_type=pt.id_product_type and p.barcode = ?";
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
	            p.setImage(rs.getString(6));
	            Category c = new Category();
	            c.setId_category(rs.getInt(7));
	            c.setName(rs.getString(8));
	            ProductType pt = new ProductType();
	            pt.setId_product_type(rs.getInt(9));
	            pt.setName(rs.getString(10));
	            pt.setPresentation(rs.getString(11));
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
		String query = "select p.id_product, p.name, p.brand, p.quantity, p.measure_unit, p.image, c.id_category, c.name, pt.id_product_type, pt.name, pt.presentation from product p, product_type pt, category c where pt.id_category=c.id_category and p.id_product_type=pt.id_product_type and p.id_product_type = ?";
		try {
			ps = con.prepareStatement(query);
			ps.setInt(1, productTypeId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
	          	Product p = new Product();
				p.setId_product(rs.getInt(1));
	            p.setName(rs.getString(2));
	            p.setBrand(rs.getString(3));
	            p.setQuantity(rs.getString(4));
	            p.setMeasure_unit(rs.getString(5));
	            p.setImage(rs.getString(6));
	            Category c = new Category();
	            c.setId_category(rs.getInt(7));
	            c.setName(rs.getString(8));
	            ProductType pt = new ProductType();
	            pt.setId_product_type(rs.getInt(9));
	            pt.setName(rs.getString(10));
	            pt.setPresentation(rs.getString(11));
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

}
