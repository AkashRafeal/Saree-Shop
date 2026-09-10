import React, { useState } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Header } from '@/components/layout/Header';
import { 
  Instagram, 
  Facebook, 
  Youtube, 
  MapPin, 
  Phone, 
  Mail, 
  Check
} from 'lucide-react';

export const CustomerLayout: React.FC = () => {
  const location = useLocation();
  const hideFooter = location.pathname === '/login' || location.pathname === '/register';

  const [newsletterEmail, setNewsletterEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);

  const handleSubscribe = (e: React.FormEvent) => {
    e.preventDefault();
    if (newsletterEmail.trim()) {
      setSubscribed(true);
      setNewsletterEmail('');
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-white text-stone-800">
      <Header />

      {/* Main Content Area */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Luxury Footer (Hidden on Login & Register Pages) */}
      {!hideFooter && (
        <footer className="bg-stone-50 border-t border-stone-200 text-stone-600 text-xs pt-7 pb-4">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 mb-5">
            {/* Column 1: About SareeAura */}
            <div className="space-y-2.5">
              <Link
                to="/"
                onClick={() => {
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                aria-label="SareeAura Home"
                className="inline-flex flex-col cursor-pointer select-none no-underline hover:no-underline border-none bg-transparent outline-none focus:outline-none focus-visible:outline-none transition-opacity duration-200 hover:opacity-90 group"
              >
                <span className="font-serif text-lg font-bold text-stone-900 uppercase tracking-wider block leading-tight select-none">
                  Saree<span className="text-[#D81B60]">Aura</span>
                </span>
                <span className="text-[8.5px] uppercase tracking-[0.2em] text-stone-400 block font-sans select-none">
                  The Festive & Bridal Edit
                </span>
              </Link>
              <p className="text-stone-500 leading-snug text-[10.5px]">
                Premium artisanal sarees blending traditional craftsmanship with modern silhouettes.
              </p>

              {/* Social Icons */}
              <div className="flex items-center space-x-2 text-stone-400">
                <a href="#instagram" aria-label="Instagram" className="w-7 h-7 rounded-full bg-white border border-stone-200 flex items-center justify-center hover:text-[#D81B60] hover:border-[#D81B60] transition shadow-2xs">
                  <Instagram className="w-3.5 h-3.5" />
                </a>
                <a href="#facebook" aria-label="Facebook" className="w-7 h-7 rounded-full bg-white border border-stone-200 flex items-center justify-center hover:text-[#D81B60] hover:border-[#D81B60] transition shadow-2xs">
                  <Facebook className="w-3.5 h-3.5" />
                </a>
                <a href="#youtube" aria-label="YouTube" className="w-7 h-7 rounded-full bg-white border border-stone-200 flex items-center justify-center hover:text-[#D81B60] hover:border-[#D81B60] transition shadow-2xs">
                  <Youtube className="w-3.5 h-3.5" />
                </a>
              </div>

              {/* Payment Methods */}
              <div className="pt-0.5">
                <div className="flex items-center space-x-1.5 text-stone-500">
                  <span className="px-1.5 py-0.5 bg-white border border-stone-200 rounded text-[9px] font-bold font-mono">VISA</span>
                  <span className="px-1.5 py-0.5 bg-white border border-stone-200 rounded text-[9px] font-bold font-mono">MasterCard</span>
                  <span className="px-1.5 py-0.5 bg-white border border-stone-200 rounded text-[9px] font-bold font-mono">RuPay</span>
                  <span className="px-1.5 py-0.5 bg-white border border-stone-200 rounded text-[9px] font-bold font-mono">UPI</span>
                </div>
              </div>
            </div>

            {/* Column 2: Shop Online */}
            <div>
              <h4 className="font-serif text-xs font-bold text-stone-900 uppercase tracking-wider mb-2">
                Shop Online
              </h4>
              <ul className="space-y-1.5 text-[11px] text-stone-500">
                <li><Link to="/shop?sort=newest" className="hover:text-[#D81B60] transition">New Arrivals</Link></li>
                <li><Link to="/shop?category=kanchipuram-silk" className="hover:text-[#D81B60] transition">Pure Silk Sarees</Link></li>
                <li><Link to="/shop?category=banarasi-silk" className="hover:text-[#D81B60] transition">Designer Banarasi</Link></li>
                <li><Link to="/shop?category=bridal-sarees" className="hover:text-[#D81B60] transition">Bridal Trousseau</Link></li>
                <li><Link to="/shop?occasion=Festive" className="hover:text-[#D81B60] transition">Festive Collection</Link></li>
                <li><Link to="/shop" className="hover:text-[#D81B60] transition">Exclusive Sale</Link></li>
              </ul>
            </div>

            {/* Column 3: Customer Care */}
            <div>
              <h4 className="font-serif text-xs font-bold text-stone-900 uppercase tracking-wider mb-2">
                Customer Care
              </h4>
              <ul className="space-y-1.5 text-[11px] text-stone-500">
                <li><Link to="/my-orders" className="hover:text-[#D81B60] transition">Track Order</Link></li>
                <li><Link to="/my-orders" className="hover:text-[#D81B60] transition">Return & Exchange</Link></li>
                <li><Link to="/shop" className="hover:text-[#D81B60] transition">Shipping Policy</Link></li>
                <li><Link to="/shop" className="hover:text-[#D81B60] transition">Terms & Conditions</Link></li>
                <li><Link to="/shop" className="hover:text-[#D81B60] transition">Privacy Policy</Link></li>
                <li><a href="mailto:care@sareeaura.com" className="hover:text-[#D81B60] transition">Contact Us</a></li>
              </ul>
            </div>

            {/* Column 4: Stay Connected */}
            <div className="space-y-2">
              <h4 className="font-serif text-xs font-bold text-stone-900 uppercase tracking-wider">
                Stay Connected
              </h4>
              <p className="text-[10.5px] text-stone-500 leading-snug">
                Receive exclusive festive previews & VIP discounts.
              </p>

              {/* Subscribe Box */}
              {subscribed ? (
                <div className="p-2 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-lg text-[10px] flex items-center space-x-1.5 font-medium">
                  <Check className="w-3 h-3 text-emerald-600" />
                  <span>Subscribed to VIP previews!</span>
                </div>
              ) : (
                <form onSubmit={handleSubscribe} className="relative flex items-center">
                  <input
                    type="email"
                    required
                    value={newsletterEmail}
                    onChange={(e) => setNewsletterEmail(e.target.value)}
                    placeholder="Enter your email"
                    className="w-full pl-3.5 pr-16 py-2 bg-white border border-stone-300 focus:border-[#D81B60] rounded-full text-[11px] focus:outline-none transition shadow-inner"
                  />
                  <button
                    type="submit"
                    className="absolute right-1 bg-[#D81B60] hover:bg-[#C2185B] text-white font-semibold text-[10px] px-3.5 py-1.5 rounded-full transition shadow-sm tracking-wider cursor-pointer"
                  >
                    JOIN
                  </button>
                </form>
              )}

              <div className="space-y-1 pt-1 text-[10.5px] text-stone-500">
                <div className="flex items-center space-x-1.5">
                  <MapPin className="w-3 h-3 text-[#D81B60] shrink-0" />
                  <span className="truncate">T. Nagar, Chennai - 600017</span>
                </div>
                <div className="flex items-center space-x-1.5">
                  <Phone className="w-3 h-3 text-[#D81B60] shrink-0" />
                  <span>+91 98765 43210</span>
                </div>
                <div className="flex items-center space-x-1.5">
                  <Mail className="w-3 h-3 text-[#D81B60] shrink-0" />
                  <span>care@sareeaura.com</span>
                </div>
              </div>
            </div>
          </div>

          {/* Popular Searches Bar */}
          <div className="border-t border-stone-200 pt-3 pb-2 text-center">
            <p className="text-[9px] text-stone-400 leading-snug uppercase tracking-wider">
              <strong>POPULAR SEARCHES:</strong> Banarasi Silk • Kanchipuram Silk • Party Wear • Organza • Floral Sarees • Daily Cotton • Wedding Lehengas • Chanderi • Tussar • Georgette • Embroidered • Silk Mark
            </p>
          </div>

          {/* Copyright */}
          <div className="border-t border-stone-200 pt-2 text-center text-[10px] text-stone-400">
            © {new Date().getFullYear()} SareeAura Fashion. All Rights Reserved. Pure Handloom Heritage.
          </div>
        </div>
      </footer>
      )}
    </div>
  );
};
