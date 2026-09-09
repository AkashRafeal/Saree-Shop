import React from 'react';
import { Sparkles, ShieldCheck, Database, Layers, ArrowRight, CheckCircle2 } from 'lucide-react';

export const HomePage: React.FC = () => {
  return (
    <div className="space-y-16 py-12 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
      {/* Hero Section */}
      <section className="relative rounded-2xl overflow-hidden bg-gradient-to-r from-stone-900 via-brand-maroon-dark to-brand-maroon text-white p-8 md:p-16 shadow-2xl border border-brand-gold/20">
        <div className="max-w-2xl space-y-6">
          <div className="inline-flex items-center space-x-2 bg-brand-gold/20 backdrop-blur-sm border border-brand-gold/40 px-3 py-1.5 rounded-full text-brand-gold-light text-xs font-semibold tracking-wider uppercase">
            <Sparkles className="w-4 h-4 text-brand-gold" />
            <span>Phase 1 Architecture Complete</span>
          </div>

          <h1 className="font-serif text-4xl sm:text-5xl lg:text-6xl font-bold leading-tight tracking-tight">
            Timeless Heritage, <br />
            <span className="gold-shimmer">Unrivaled Grandeur.</span>
          </h1>

          <p className="text-stone-300 text-base sm:text-lg leading-relaxed font-light">
            Welcome to SareeAura — an artisanal journey celebrating authentic Indian handlooms. 
            Engineered with a Spring Boot Modular Monolith architecture, React 18, and MySQL 8 persistence.
          </p>

          <div className="flex flex-wrap gap-4 pt-2">
            <a
              href="#phase-status"
              className="inline-flex items-center space-x-2 bg-brand-gold hover:bg-brand-gold-dark text-stone-900 font-semibold px-6 py-3 rounded-lg shadow-lg hover:shadow-xl transition-all"
            >
              <span>Explore Phase 1 Setup</span>
              <ArrowRight className="w-4 h-4" />
            </a>
            <a
              href="http://localhost:8080/swagger-ui.html"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-2 bg-white/10 hover:bg-white/20 text-white font-medium px-6 py-3 rounded-lg border border-white/20 backdrop-blur-sm transition-all"
            >
              <span>Swagger / OpenAPI</span>
            </a>
          </div>
        </div>
      </section>

      {/* Phase 1 Verification Grid */}
      <section id="phase-status" className="space-y-6">
        <div className="text-center space-y-2">
          <h2 className="font-serif text-3xl font-bold text-stone-900">
            Phase 1 Foundation Metrics
          </h2>
          <p className="text-stone-600 text-sm max-w-xl mx-auto">
            Core architecture verification and environment infrastructure established for the SareeAura modular platform.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 rounded-xl bg-white border border-stone-200 shadow-sm space-y-4 hover:shadow-md transition">
            <div className="w-12 h-12 rounded-lg bg-brand-maroon/10 text-brand-maroon flex items-center justify-center">
              <Layers className="w-6 h-6" />
            </div>
            <h3 className="font-serif text-xl font-bold text-stone-900">
              Modular Monolith
            </h3>
            <p className="text-xs text-stone-600 leading-relaxed">
              Organized into 18 isolated business packages under <code className="bg-stone-100 px-1 py-0.5 rounded text-brand-maroon">com.sareeaura</code>: auth, product, order, cart, inventory, payment, category, and review.
            </p>
            <div className="flex items-center text-xs text-emerald-700 font-medium">
              <CheckCircle2 className="w-4 h-4 mr-1.5" />
              <span>Clean package isolation</span>
            </div>
          </div>

          <div className="p-6 rounded-xl bg-white border border-stone-200 shadow-sm space-y-4 hover:shadow-md transition">
            <div className="w-12 h-12 rounded-lg bg-brand-emerald/10 text-brand-emerald flex items-center justify-center">
              <Database className="w-6 h-6" />
            </div>
            <h3 className="font-serif text-xl font-bold text-stone-900">
              MySQL 8 Database
            </h3>
            <p className="text-xs text-stone-600 leading-relaxed">
              Configured with UTF8MB4 Unicode charset, JPA Auditing, transaction management, connection pooling, and initial schema definitions.
            </p>
            <div className="flex items-center text-xs text-emerald-700 font-medium">
              <CheckCircle2 className="w-4 h-4 mr-1.5" />
              <span>sareeaura_db active</span>
            </div>
          </div>

          <div className="p-6 rounded-xl bg-white border border-stone-200 shadow-sm space-y-4 hover:shadow-md transition">
            <div className="w-12 h-12 rounded-lg bg-amber-500/10 text-amber-700 flex items-center justify-center">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h3 className="font-serif text-xl font-bold text-stone-900">
              Security & Environment
            </h3>
            <p className="text-xs text-stone-600 leading-relaxed">
              Stateless JWT authentication filter chain, BCrypt password hashing, Razorpay test mode stubs, and .env configuration templates.
            </p>
            <div className="flex items-center text-xs text-emerald-700 font-medium">
              <CheckCircle2 className="w-4 h-4 mr-1.5" />
              <span>Spring Security 6 configured</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};
