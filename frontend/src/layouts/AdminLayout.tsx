import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Package, 
  Layers, 
  Users, 
  ShoppingCart, 
  Boxes, 
  TicketPercent, 
  Star, 
  Image as ImageIcon, 
  BarChart3, 
  Settings,
  ExternalLink
} from 'lucide-react';

export const AdminLayout: React.FC = () => {
  const location = useLocation();

  const navigation = [
    { name: 'Dashboard', href: '/admin', icon: LayoutDashboard },
    { name: 'Products', href: '/admin/products', icon: Package },
    { name: 'Categories', href: '/admin/categories', icon: Layers },
    { name: 'Orders', href: '/admin/orders', icon: ShoppingCart },
    { name: 'Inventory', href: '/admin/inventory', icon: Boxes },
    { name: 'Customers', href: '/admin/customers', icon: Users },
    { name: 'Coupons', href: '/admin/coupons', icon: TicketPercent },
    { name: 'Reviews', href: '/admin/reviews', icon: Star },
    { name: 'Banners', href: '/admin/banners', icon: ImageIcon },
    { name: 'Reports', href: '/admin/reports', icon: BarChart3 },
    { name: 'Settings', href: '/admin/settings', icon: Settings },
  ];

  return (
    <div className="min-h-screen flex bg-stone-100 text-stone-800">
      {/* Sidebar */}
      <aside className="w-64 bg-stone-900 text-stone-200 flex flex-col border-r border-stone-800">
        <div className="h-20 flex items-center px-6 border-b border-stone-800 bg-stone-950">
          <Link to="/admin" className="flex flex-col">
            <span className="font-serif text-xl font-bold tracking-wider text-brand-gold uppercase">
              Saree<span className="text-white">Aura</span>
            </span>
            <span className="text-[10px] tracking-widest text-stone-400 font-sans uppercase">
              Admin Portal
            </span>
          </Link>
        </div>

        <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
          {navigation.map((item) => {
            const isActive = location.pathname === item.href;
            const Icon = item.icon;
            return (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-brand-maroon text-white shadow-sm'
                    : 'text-stone-400 hover:text-white hover:bg-stone-800'
                }`}
              >
                <Icon className="w-4 h-4 mr-3" />
                {item.name}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-stone-800">
          <Link
            to="/"
            target="_blank"
            className="flex items-center justify-center px-4 py-2 text-xs font-medium text-brand-gold bg-stone-800/80 hover:bg-stone-800 rounded-md transition"
          >
            <span>View Storefront</span>
            <ExternalLink className="w-3 h-3 ml-1.5" />
          </Link>
        </div>
      </aside>

      {/* Main Admin Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-16 bg-white border-b border-stone-200 px-6 flex items-center justify-between shadow-sm">
          <h1 className="text-lg font-semibold text-stone-800">
            SareeAura Administration
          </h1>
          <div className="flex items-center space-x-4">
            <span className="text-xs bg-emerald-100 text-emerald-800 font-medium px-2.5 py-1 rounded-full border border-emerald-200">
              System Live
            </span>
            <span className="text-xs text-stone-500">
              Admin: admin@sareeaura.com
            </span>
          </div>
        </header>

        <main className="flex-1 p-6 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
