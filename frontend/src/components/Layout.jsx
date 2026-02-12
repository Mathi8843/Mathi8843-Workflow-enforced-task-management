import { useNavigate, useLocation, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Button } from "./Button";
import { LogOut, LayoutDashboard, User } from "lucide-react";
import { cn } from "../lib/utils";

export default function Layout({ children }) {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const navItems = [
        { name: "Board", path: "/", icon: LayoutDashboard },
        // { name: "All Tasks", path: "/tasks", icon: CheckSquare }, // Future
        // { name: "Users", path: "/users", icon: Users }, // Future
    ];

    return (
        <div className="min-h-screen bg-gray-50 flex">
            {/* Sidebar - Desktop */}
            <div className="hidden md:flex md:w-64 md:flex-col fixed inset-y-0 z-50 bg-white border-r border-gray-200">
                <div className="flex items-center h-16 shrink-0 px-6 border-b border-gray-200">
                    <LayoutDashboard className="h-6 w-6 text-primary-600 mr-2" />
                    <h1 className="text-xl font-bold text-gray-900">TaskApp</h1>
                </div>
                <div className="flex flex-col flex-grow overflow-y-auto pt-5 pb-4 px-4">
                    <nav className="flex-1 space-y-1">
                        {navItems.map((item) => {
                            const Icon = item.icon;
                            const isActive = location.pathname === item.path;
                            return (
                                <Link
                                    key={item.name}
                                    to={item.path}
                                    className={cn(
                                        "group flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors",
                                        isActive
                                            ? "bg-primary-50 text-primary-600"
                                            : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                                    )}
                                >
                                    <Icon className={cn("mr-3 h-5 w-5 flex-shrink-0 transition-colors", isActive ? "text-primary-600" : "text-gray-400 group-hover:text-gray-500")} />
                                    {item.name}
                                </Link>
                            );
                        })}
                    </nav>
                </div>
                <div className="border-t border-gray-200 p-4">
                    <div className="flex items-center mb-4 px-2">
                        <div className="h-8 w-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700">
                            <User className="h-5 w-5" />
                        </div>
                        <div className="ml-3">
                            <p className="text-sm font-medium text-gray-700">{user?.displayName || "User"}</p>
                            <p className="text-xs text-gray-500 truncate w-32">{user?.email}</p>
                        </div>
                    </div>
                    <Button variant="outline" className="w-full justify-start text-gray-600" onClick={handleLogout}>
                        <LogOut className="h-4 w-4 mr-2" />
                        Logout
                    </Button>
                </div>
            </div>

            {/* Main Content */}
            <div className="flex flex-1 flex-col md:pl-64 transition-all duration-300">
                {/* Mobile Header (simplified) */}
                <div className="md:hidden flex items-center justify-between bg-white border-b border-gray-200 px-4 py-3">
                    <div className="flex items-center">
                        <LayoutDashboard className="h-6 w-6 text-primary-600 mr-2" />
                        <span className="font-bold text-gray-900">TaskApp</span>
                    </div>
                    <Button size="sm" variant="ghost" onClick={handleLogout}>
                        <LogOut className="h-4 w-4" />
                    </Button>
                </div>

                <main className="flex-1 py-8 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full">
                    {children}
                </main>
            </div>
        </div>
    );
}
