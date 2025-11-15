import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpErrorResponse, HttpClient } from '@angular/common/http';

// Inline models to keep this component self-contained
export interface Product {
  sku: string;
  name: string;
  price: number;
  quantity: number;
  category: string;
}

export interface Order {
  orderNumber: string;
  customerId: string;
  totalAmount: number;
  status: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="container mt-5">
      <div class="row">
        <div class="col-md-12">
          <h1>🛍️ QuickCommerce</h1>
          <p class="text-muted">Enterprise E-Commerce Platform</p>
        </div>
      </div>

      <ul class="nav nav-tabs mb-4">
        <li class="nav-item">
          <a class="nav-link" [class.active]="currentTab === 'products'" 
             (click)="currentTab = 'products'">Products</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [class.active]="currentTab === 'orders'" 
             (click)="currentTab = 'orders'">Orders</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" [class.active]="currentTab === 'about'" 
             (click)="currentTab = 'about'">About</a>
        </li>
      </ul>

      <!-- PRODUCTS TAB -->
      <div *ngIf="currentTab === 'products'" class="tab-content">
        <div class="card">
          <div class="card-header">
            <h5>Products</h5>
          </div>
          <div class="card-body">
            <button class="btn btn-primary mb-3" [disabled]="loadingProducts" (click)="loadProducts()">
              {{ loadingProducts ? 'Loading…' : 'Load Products' }}
            </button>

            <div *ngIf="loadingProducts" class="alert alert-info">Loading...</div>
            
            <table class="table table-striped" *ngIf="products.length > 0">
              <thead>
                <tr>
                  <th>SKU</th>
                  <th>Name</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Category</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let product of products; trackBy: trackByProduct">
                  <td>{{ product.sku }}</td>
                  <td>{{ product.name }}</td>
                  <td>{{ product.price | currency }}</td>
                  <td>{{ product.quantity }}</td>
                  <td>{{ product.category }}</td>
                </tr>
              </tbody>
            </table>
            <p *ngIf="!loadingProducts && products.length === 0" class="text-muted">No products loaded. Click "Load Products" to fetch from API.</p>
          </div>
        </div>
      </div>

      <!-- ORDERS TAB -->
      <div *ngIf="currentTab === 'orders'" class="tab-content">
        <div class="card">
          <div class="card-header">
            <h5>Orders</h5>
          </div>
          <div class="card-body">
            <button class="btn btn-success mb-3" [disabled]="loadingOrders" (click)="loadOrders()">
              {{ loadingOrders ? 'Loading…' : 'Load Orders' }}
            </button>

            <div *ngIf="loadingOrders" class="alert alert-info">Loading...</div>

            <table class="table table-striped" *ngIf="orders.length > 0">
              <thead>
                <tr>
                  <th>Order #</th>
                  <th>Customer</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let order of orders; trackBy: trackByOrder">
                  <td>{{ order.orderNumber }}</td>
                  <td>{{ order.customerId }}</td>
                  <td>{{ order.totalAmount | currency }}</td>
                  <td>{{ order.status }}</td>
                </tr>
              </tbody>
            </table>
            <p *ngIf="!loadingOrders && orders.length === 0" class="text-muted">No orders loaded. Click "Load Orders" to fetch from API.</p>
          </div>
        </div>
      </div>

      <!-- ABOUT TAB -->
      <div *ngIf="currentTab === 'about'" class="tab-content">
        <div class="card">
          <div class="card-header">
            <h5>About QuickCommerce</h5>
          </div>
          <div class="card-body">
            <h6>Modern Enterprise E-Commerce Platform</h6>
            <ul>
              <li>✅ 5 Microservices (Java 21 + Spring Boot 3.2)</li>
              <li>✅ Angular 17 Frontend</li>
              <li>✅ Apache Kafka Event Streaming</li>
              <li>✅ PostgreSQL Databases</li>
              <li>✅ Eureka Service Discovery</li>
              <li>✅ Complete Docker Setup</li>
            </ul>

            <h6 class="mt-4">API Endpoints Available At:</h6>
            <p><code>http://localhost:8000/swagger-ui.html</code></p>

            <h6>Infrastructure URLs:</h6>
            <ul>
              <li>Eureka: <code>http://localhost:8761</code></li>
              <li>Kafka UI: <code>http://localhost:9080</code></li>
              <li>PgAdmin: <code>http://localhost:5050</code></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container {
      max-width: 1200px;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }
    h1 {
      color: #333;
      font-weight: bold;
    }
    .card {
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    .nav-link {
      cursor: pointer;
    }
    .nav-link.active {
      font-weight: bold;
      border-bottom: 3px solid #007bff;
    }
  `]
})
export class AppComponent implements OnInit {
  currentTab = 'products';
  products: Product[] = [];
  orders: Order[] = [];

  loadingProducts = false;
  loadingOrders = false;

  private readonly apiBase = 'http://localhost:8000';
  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.loadingProducts = true;
    this.http.get<Product[]>(`${this.apiBase}/api/products`).subscribe({
      next: (data) => {
        this.products = data ?? [];
        this.loadingProducts = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading products:', error.message, error);
        this.loadingProducts = false;
        alert('Error loading products. Make sure backend is running!');
      }
    });
  }

  loadOrders() {
    this.loadingOrders = true;
    this.http.get<Order[]>(`${this.apiBase}/api/orders`).subscribe({
      next: (data) => {
        this.orders = data ?? [];
        this.loadingOrders = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading orders:', error.message, error);
        this.loadingOrders = false;
        alert('Error loading orders. Make sure backend is running!');
      }
    });
  }

  trackByProduct = (_: number, p: Product) => p?.sku ?? _;
  trackByOrder = (_: number, o: Order) => o?.orderNumber ?? _;
}