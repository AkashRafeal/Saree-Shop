import React, { useState, useRef, useEffect } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
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
  ExternalLink,
  ChevronDown,
  LogOut,
  ShieldCheck
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';

export const AdminLayout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const [isProfileDropdownOpen, setIsProfileDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown on click outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setIsProfileDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    setIsProfileDropdownOpen(false);
    logout();
    navigate('/login');
  };

  const displayName = user?.firstName ? `${user.firstName} ${user.lastName || ''}`.trim() : 'Administrator';
  const displayEmail = user?.email || 'admin@sareeaura.com';
  const avatarLetter = (user?.firstName?.[0] || 'A').toUpperCase();

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
    <div className="min-h-screen flex bg-[#FAF8F5] text-stone-800 font-sans">
      {/* Sidebar */}
      <aside className="w-64 bg-stone-950 text-stone-200 flex flex-col border-r border-stone-800/80 shadow-xl">
        <div className="h-20 flex items-center px-6 border-b border-stone-800/80 bg-stone-950/90 backdrop-blur-sm">
          <Link
            to="/admin"
            onClick={() => {
              window.scrollTo({ top: 0, behavior: 'smooth' });
            }}
            aria-label="Return to Admin Dashboard"
            className="flex flex-col group cursor-pointer select-none no-underline hover:no-underline border-none bg-transparent outline-none focus:outline-none focus-visible:outline-none transition-opacity duration-200 hover:opacity-90"
          >
            <div className="flex items-center gap-1.5">
              <span className="font-serif text-xl font-bold tracking-wider text-white uppercase select-none">
                Saree<span className="text-[#D81B60]">Aura</span>
              </span>
              <span className="w-1.5 h-1.5 rounded-full bg-[#D4AF37]"></span>
            </div>
            <span className="text-[9px] tracking-[0.25em] text-[#D4AF37] font-sans uppercase font-medium mt-0.5 select-none">
              Atelier Administration
            </span>
          </Link>
        </div>

        <nav className="flex-1 px-3 py-6 space-y-1.5 overflow-y-auto">
          {navigation.map((item) => {
            const isActive = location.pathname === item.href;
            const Icon = item.icon;
            return (
              <Link
                key={item.name}
                to={item.href}
                className={`flex items-center px-3.5 py-2.5 rounded-xl text-xs font-semibold tracking-wide transition-all duration-200 ${
                  isActive
                    ? 'bg-gradient-to-r from-[#D81B60] to-[#E11D48] text-white shadow-lg shadow-[#D81B60]/30 scale-[1.02]'
                    : 'text-stone-400 hover:text-white hover:bg-stone-900/90'
                }`}
              >
                <Icon className={`w-4 h-4 mr-3 ${isActive ? 'text-white' : 'text-stone-400'}`} />
                {item.name}
              </Link>
            );
          })}
        </nav>
      </aside>

      {/* Main Admin Content Area */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-16 bg-white border-b border-stone-200/80 px-8 flex items-center justify-between shadow-xs sticky top-0 z-30">
          <div className="flex items-center gap-3">
            <Link
              to="/admin"
              className="font-serif text-lg font-bold text-stone-900 tracking-tight no-underline hover:text-[#D81B60] transition-colors cursor-pointer"
            >
              SareeAura Administration
            </Link>
            <span className="hidden md:inline-block text-[11px] font-sans font-medium text-stone-400 bg-stone-100 px-2.5 py-0.5 rounded-full">
              v1.0 • Luxury Atelier
            </span>
          </div>
          <div className="flex items-center space-x-4">
            <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200/70 shadow-xs">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
              </span>
              System Live
            </span>

            <Link
              to="/"
              target="_blank"
              className="inline-flex items-center gap-1.5 px-4 py-1.5 text-xs font-semibold text-stone-700 hover:text-[#D81B60] bg-stone-100 hover:bg-[#FFF0F5] border border-stone-200/80 hover:border-[#D81B60]/30 rounded-full transition-all duration-200 shadow-xs group"
              title="Open customer storefront in a new tab"
            >
              <span>View Storefront</span>
              <ExternalLink className="w-3.5 h-3.5 text-stone-400 group-hover:text-[#D81B60] transition-transform group-hover:translate-x-0.5" />
            </Link>

            {/* Profile Dropdown Container */}
            <div className="relative pl-4 border-l border-stone-200" ref={dropdownRef}>
              <button
                type="button"
                onClick={() => setIsProfileDropdownOpen(!isProfileDropdownOpen)}
                className="flex items-center gap-2.5 p-1 pr-2 rounded-full hover:bg-stone-50 transition cursor-pointer select-none focus:outline-none focus:ring-2 focus:ring-[#D81B60]/20"
                aria-expanded={isProfileDropdownOpen}
                aria-haspopup="true"
              >
                <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#D81B60] to-[#AD1457] text-white flex items-center justify-center font-bold text-xs shadow-xs">
                  {avatarLetter}
                </div>
                <div className="hidden sm:flex flex-col text-left">
                  <span className="text-xs font-semibold text-stone-800 leading-tight">
                    {displayName}
                  </span>
                  <span className="text-[10px] text-stone-400 font-mono">
                    {displayEmail}
                  </span>
                </div>
                <ChevronDown className={`w-3.5 h-3.5 text-stone-400 transition-transform duration-200 ${isProfileDropdownOpen ? 'rotate-180' : ''}`} />
              </button>

              {/* Profile Dropdown Menu */}
              {isProfileDropdownOpen && (
                <div className="absolute right-0 top-full mt-2 w-64 bg-white rounded-2xl shadow-xl border border-stone-200/90 py-2 z-50 animate-fadeIn">
                  {/* User Profile Header */}
                  <div className="px-4 py-2.5 border-b border-stone-100 flex items-center gap-3">
                    <div className="w-9 h-9 rounded-full bg-gradient-to-br from-[#D81B60] to-[#AD1457] text-white flex items-center justify-center font-bold text-sm shadow-xs shrink-0">
                      {avatarLetter}
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-xs font-bold text-stone-900 truncate">
                        {displayName}
                      </p>
                      <p className="text-[10px] text-stone-400 font-mono truncate">
                        {displayEmail}
                      </p>
                      <span className="inline-flex items-center gap-1 mt-1 text-[9px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-[#D81B60]/10 text-[#D81B60]">
                        <ShieldCheck className="w-2.5 h-2.5" />
                        {user?.role === 'ROLE_ADMIN' ? 'Super Admin' : 'Admin'}
                      </span>
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="py-1">
                    <Link
                      to="/admin/settings"
                      onClick={() => setIsProfileDropdownOpen(false)}
                      className="flex items-center px-4 py-2 text-xs font-medium text-stone-700 hover:bg-[#FAF8F5] hover:text-[#D81B60] transition gap-2.5"
                    >
                      <Settings className="w-4 h-4 text-stone-400" />
                      <span>Admin Settings</span>
                    </Link>
                    <Link
                      to="/"
                      target="_blank"
                      onClick={() => setIsProfileDropdownOpen(false)}
                      className="flex items-center px-4 py-2 text-xs font-medium text-stone-700 hover:bg-[#FAF8F5] hover:text-[#D81B60] transition gap-2.5"
                    >
                      <ExternalLink className="w-4 h-4 text-stone-400" />
                      <span>View Storefront</span>
                    </Link>
                  </div>

                  <div className="border-t border-stone-100 my-1" />

                  {/* Logout Action */}
                  <div className="px-1">
                    <button
                      type="button"
                      onClick={handleLogout}
                      className="flex items-center w-full px-3 py-2 text-xs font-semibold text-rose-600 hover:bg-rose-50 hover:text-rose-700 rounded-xl transition gap-2.5 cursor-pointer"
                    >
                      <LogOut className="w-4 h-4 text-rose-500" />
                      <span>Sign Out</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>

        <main className="flex-1 p-8 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
